package inject;

import com.google.inject.Provider;
import common.cms.CmsService;
import common.cms.PlayCmsService;
import play.Logger;
import play.i18n.MessagesApi;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
class CmsServiceProvider implements Provider<CmsService> {
    private final MessagesApi messages;

    @Inject
    private CmsServiceProvider(final MessagesApi messages) {
        this.messages = messages;
    }

    @Override
    public CmsService get() {
        Logger.info("execute CmsServiceProvider.get()");
        return PlayCmsService.of(messages);
    }
}
