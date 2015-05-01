package com.nocomment.taylor.tasks.models;

import android.os.Parcel;
import android.os.Parcelable;


public class Task implements Parcelable {

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public int id;

    public String taskName;
    public String dueDate;
    public String location;
    public String notes;

    public boolean completed;
    public boolean cleared;
    public boolean deleted;

    public Task() {

    }

    public Task(Parcel source) {
        id = source.readInt();
        taskName = source.readString();
        dueDate = source.readString();
        location = source.readString();
        notes = source.readString();
        completed = source.readInt() != 0;
        cleared = source.readInt() != 0;
        deleted = source.readInt() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(taskName);
        dest.writeString(dueDate);
        dest.writeString(location);
        dest.writeString(notes);
        dest.writeInt(completed ? 1 : 0);
        dest.writeInt(cleared ? 1 : 0);
        dest.writeInt(deleted ? 1 : 0);
    }
}
