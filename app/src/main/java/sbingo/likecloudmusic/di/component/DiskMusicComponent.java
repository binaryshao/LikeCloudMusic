package sbingo.likecloudmusic.di.component;

import dagger.Component;
import sbingo.likecloudmusic.di.module.DiskMusicModule;
import sbingo.likecloudmusic.ui.fragment.LocalMusic.DiskMusicFragment;

/**
 * Author: Sbingo
 * Date:   2016/12/19
 */

@Component(modules = DiskMusicModule.class)
public interface DiskMusicComponent {
     void inject(DiskMusicFragment diskMusicFragment);
}
