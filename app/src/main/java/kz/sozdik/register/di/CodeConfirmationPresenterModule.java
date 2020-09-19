package kz.sozdik.register.di;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import kz.sozdik.di.scopes.Presenter;

@Module
public class CodeConfirmationPresenterModule {

    String confirmationToken;

    @Inject
    public CodeConfirmationPresenterModule(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    @Provides
    @Presenter
    String getConfirmationToken() {
        return confirmationToken;
    }
}
