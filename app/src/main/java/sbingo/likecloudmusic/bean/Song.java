package sbingo.likecloudmusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: Sbingo
 * Date:   2016/12/15
 */

public class Song implements Parcelable {

    protected Song(Parcel in) {
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
