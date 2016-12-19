package sbingo.likecloudmusic.di.module;

import dagger.Module;
import dagger.Provides;
import sbingo.likecloudmusic.interactor.DiskMusicInteractor;
import sbingo.likecloudmusic.ui.view.DiskMusicView;

/**
 * Author: Sbingo
 * Date:   2016/12/19
 */
@Module
public class DiskMusicModule {

    private DiskMusicInteractor interactor;

    private DiskMusicView view;

    public DiskMusicModule(DiskMusicInteractor interactor, DiskMusicView view) {
        this.interactor = interactor;
        this.view = view;
    }

    @Provides
    DiskMusicInteractor provideDiskMusicInteractor() {
        return interactor;
    }

    @Provides
    DiskMusicView provideDiskMusicView() {
        return view;
    }

}
