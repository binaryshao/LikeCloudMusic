package sbingo.likecloudmusic.utils;

import sbingo.likecloudmusic.ui.fragment.BaseFragment;
import sbingo.likecloudmusic.ui.fragment.LocalMusic.LocalMusicFragment;
import sbingo.likecloudmusic.ui.fragment.NetMusic.NetMusicFragment;
import sbingo.likecloudmusic.ui.fragment.NetMusic.PlaylistFragment;
import sbingo.likecloudmusic.ui.fragment.NetMusic.RadioFragment;
import sbingo.likecloudmusic.ui.fragment.NetMusic.RankingFragment;
import sbingo.likecloudmusic.ui.fragment.NetMusic.RecommendFragment;
import sbingo.likecloudmusic.ui.fragment.Social.FriendsFragment;
import sbingo.likecloudmusic.ui.fragment.Social.NearbyFragment;
import sbingo.likecloudmusic.ui.fragment.Social.NewStateFragment;
import sbingo.likecloudmusic.ui.fragment.Social.SocialFragment;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public class NavigationUtils {

    public static BaseFragment[] mainFragments = {NetMusicFragment.getInstance(), LocalMusicFragment.getInstance(), SocialFragment.getInstance()};

    public static BaseFragment[] netMusicFragments = {RecommendFragment.getInstance(), PlaylistFragment.getInstance(), RadioFragment.getInstance(), RankingFragment.getInstance()};

    public static BaseFragment[] socialFragments = {NewStateFragment.getInstance(), NearbyFragment.getInstance(), FriendsFragment.getInstance()};
}
