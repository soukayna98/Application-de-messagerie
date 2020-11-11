package com.eschoolproject.quattus;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class SectionsPagerAdapter extends FragmentPagerAdapter {

    private String request="",chat="",friends="";

    public SectionsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public SectionsPagerAdapter(@NonNull FragmentManager fm, String Request, String Chat, String Friends) {
        super(fm);
        request= Request;
        chat = Chat;
        friends = Friends;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0 : RequestFragment requestFragment= new RequestFragment();
            return requestFragment;

            case 1 : ChatFragment chatFragment = new ChatFragment();
                return chatFragment;

            case 2 : FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

                default: return  null;
        }


    }

    @Override
    public int getCount() {
        return 3;
    }

    public String getPageTitle(int position){

        switch (position) {

            case 0 : return request ;// "Fajii0";
            case 1 : return chat ;// "Fajii 1";
            case 2 : return friends; //"Fajii 2";
            default: return  null;
        }
    }
}
