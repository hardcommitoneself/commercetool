package common.pages;

import play.mvc.Call;

public interface ReverseRouter {

    Call category(final String language, final String slug, final int page);
}
