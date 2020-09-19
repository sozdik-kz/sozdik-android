package kz.sozdik.favorites.di;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import kz.sozdik.di.scopes.Presenter;

@Module
public class FavoritesPresenterModule {

    String langFrom;

    @Inject
    public FavoritesPresenterModule(String langFrom) {
        this.langFrom = langFrom;
    }

    @Provides
    @Presenter
    String getLangFrom() {
        return langFrom;
    }
}
