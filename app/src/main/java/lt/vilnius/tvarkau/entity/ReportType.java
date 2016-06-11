package lt.vilnius.tvarkau.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ReportType implements Parcelable {

    private String name;

    public ReportType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }


    protected ReportType(Parcel in) {
        this.name = in.readString();
    }

    public static final Parcelable.Creator<ReportType> CREATOR = new Parcelable.Creator<ReportType>() {
        @Override
        public ReportType createFromParcel(Parcel source) {
            return new ReportType(source);
        }

        @Override
        public ReportType[] newArray(int size) {
            return new ReportType[size];
        }
    };
}
