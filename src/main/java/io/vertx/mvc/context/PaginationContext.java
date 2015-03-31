package io.vertx.mvc.context;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.mvc.exceptions.BadRequestException;

import java.util.ArrayList;
import java.util.List;

/**
 * Reflects the pagination state for a RoutingContext (request parameters)
 * Provides service methods to :
 * - generate pagination Link headers
 * - generate paginationContext to render a page on the server
 * 
 * @author aesteve
 */
public class PaginationContext {

    public static final String DATA_ATTR = "paginationContext";

    // TODO : (to make it a generic service)
    // That's some configuration stuff, should not be found in an instanciated object (reflecting one request)
    // -> move it in config or let the user define it (set/get)
    // if it changes, clients are broken...
    public static final String CURRENT_PAGE_QUERY_PARAM = "page";
    public static final String PER_PAGE_QUERY_PARAM = "perPage";
    public static final Integer DEFAULT_PER_PAGE = 30;
    public static final Integer MAX_PER_PAGE = 100;

    private Integer pageAsked = 1;
    private Integer itemsPerPage = DEFAULT_PER_PAGE;
    private Integer totalPages; // Will be set once the request has been processed (payload)

    /**
     * Prefer using fromRoutingContext but can be instanciated directly
     * (for static page generation for instance)
     * 
     * @param pageAsked
     * @param itemsPerPage
     */
    public PaginationContext(Integer pageAsked, Integer itemsPerPage) {
        if (pageAsked != null)
            this.pageAsked = pageAsked;
        if (itemsPerPage != null)
            this.itemsPerPage = itemsPerPage;
    }

    /**
     * The preferred way to create a PaginationContext
     * 
     * @param context
     * @return
     * @throws BadRequestException
     */
    public static PaginationContext fromContext(RoutingContext context) throws BadRequestException {
        HttpServerRequest request = context.request();
        String pageStr = request.getParam(PaginationContext.CURRENT_PAGE_QUERY_PARAM);
        String perPageStr = request.getParam(PaginationContext.PER_PAGE_QUERY_PARAM);
        Integer page = null;
        Integer perPage = null;
        try {
            if (pageStr != null) {
                page = Integer.parseInt(pageStr);
            }
            if (perPageStr != null) {
                perPage = Integer.parseInt(perPageStr);
            }
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid pagination parameters : expecting integers");
        }
        if (perPage != null && perPage > PaginationContext.MAX_PER_PAGE) {
            throw new BadRequestException("Invalid " + PaginationContext.PER_PAGE_QUERY_PARAM + " parameter, max is " + PaginationContext.MAX_PER_PAGE);
        }
        return new PaginationContext(page, perPage);
    }

    public String buildLinkHeader(HttpServerRequest request) {
        List<String> links = getNavLinks(request);
        if (links == null) {
            return null;
        }
        String s = String.join(", ", links);
        return s;
    }

    public List<String> getNavLinks(HttpServerRequest request) {
        if (totalPages == null) {
            return null;
        }
        List<String> links = new ArrayList<String>();
        if (pageAsked > 1) {
            links.add(pageUrl(request, 1, "first"));
            links.add(pageUrl(request, pageAsked - 1, "prev"));
        }
        if (pageAsked < totalPages) {
            links.add(pageUrl(request, totalPages, "last"));
            links.add(pageUrl(request, pageAsked + 1, "next"));
        }
        return links;
    }

    // FIXME : refactor that in a cleaner way once I'm not tired as hell
    private String pageUrl(HttpServerRequest request, int pageNum, String rel) {
        StringBuilder sb = new StringBuilder("<");
        String url = request.absoluteURI();
        if (url.indexOf("?") == -1) { // can't rely on params() 'cause we might have injected some stuff (routing)
            url += "?" + CURRENT_PAGE_QUERY_PARAM + "=" + pageNum;
            url += "&" + PER_PAGE_QUERY_PARAM + "=" + itemsPerPage;
        } else {
            if (url.indexOf(CURRENT_PAGE_QUERY_PARAM + "=") > url.indexOf("?")) {
                url = url.replaceAll(CURRENT_PAGE_QUERY_PARAM + "=([^&]+)", CURRENT_PAGE_QUERY_PARAM + "=" + pageNum);
            } else {
                url += "&" + CURRENT_PAGE_QUERY_PARAM + "=" + pageNum;
            }
            if (url.indexOf("&" + PER_PAGE_QUERY_PARAM) == -1 && url.indexOf("?" + PER_PAGE_QUERY_PARAM) == -1) {
                url += "&" + PER_PAGE_QUERY_PARAM + "=" + itemsPerPage;
            }
        }
        sb.append(url);
        sb.append(">; ");
        sb.append("rel=\"" + rel + "\"");
        return sb.toString();
    }

    public Integer getPageAsked() {
        return pageAsked;
    }

    public Integer getItemsPerPage() {
        return itemsPerPage;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setNbItems(Integer nbTotalItems) {
        this.totalPages = (int)(nbTotalItems / itemsPerPage);
        int modulo = (int)(nbTotalItems % itemsPerPage);
        if (modulo > 0) {
            this.totalPages = this.totalPages + 1;
        }
    }
    
    public void setNbItems(Long nbTotalItems) {
    	setNbItems(nbTotalItems.intValue());
    }
    

    public boolean hasMorePages() {
        return totalPages > pageAsked;
    }

    public int firstItemInPage() {
        return itemsPerPage * (pageAsked - 1);
    }

    public int lastItemInPage() {
        return firstItemInPage() + itemsPerPage;
    }

    /**
     * Creates a JsonObject containing all pagination
     * that can be injected and evaluated
     * when rendering a page on the server
     * 
     * @return a JsonObject containing pagination informations
     */
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.put("current", pageAsked);
        json.put("next", pageAsked == totalPages ? null : pageAsked + 1);
        json.put("last", pageAsked == totalPages ? null : totalPages);
        json.put("prev", pageAsked == 1 ? null : pageAsked - 1);
        json.put("first", pageAsked == 1 ? null : 1);
        json.put("perPage", itemsPerPage);
        json.put("total", totalPages);
        return json;
    }
}
