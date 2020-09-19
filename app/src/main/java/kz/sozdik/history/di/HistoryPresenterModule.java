package kz.sozdik.history.di;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import kz.sozdik.di.scopes.Presenter;

@Module
public class HistoryPresenterModule {

    String langFrom;

    @Inject
    public HistoryPresenterModule(String langFrom) {
        this.langFrom = langFrom;
    }

    @Provides
    @Presenter
    String getLangFrom() {
        return langFrom;
    }

}
