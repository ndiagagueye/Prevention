package com.example.gueye.memoireprevention2018.utils;

//cette classe va contenir toutes nos constantes


import android.Manifest;

import com.example.gueye.memoireprevention2018.R;

public class Const {

    public static  final  String AU_SECOURS = "au secours";
    public static final  String DESCRIPTION_ALERT = "Au secours, je suis victime d'une aggression";
    //public static final String TYPE_ALERT = "Aggression";
    //public static  final  int  IMAGE_ALERT = R.drawable.violence;


    public static final int[] DEFAULT_RESOURCE_ICONES_USER = {
            R.drawable.medecin1,
            R.drawable.arts_martiaux,
            R.drawable.armee,
            R.drawable.mecanicen,
            R.drawable.besoin_medcin_image,
    };

    public static  final String [] DEFAULT_TYPES_USER = {
            "Medecin",
            "Pratiquant d'arts martiaux ",
            "Corps armée",
            "Mécanicien",
            "Autres"
    };

    public static final int[] DEFAULT_RESOURCE_ICONES = {

            R.drawable.icon_aggression_couteau,
            R.drawable.icon_vol_cambriolage,
            R.drawable.icon_viol,
            R.drawable.icon_panne_de_null_part,
            R.drawable.icon_car_accident,
            R.drawable.icon_besoin_medcin,
            R.drawable.autre_image
    };

    public static final int[] DEFAULT_RESOURCE_IMAGES = {

            R.drawable.violen,
            R.drawable.vol_image,
            R.drawable.viol,
            R.drawable.panne_de_null_part,
            R.drawable.accident_image,
            R.drawable.besoin_medcin_image,
            R.drawable.autre_image

    };

    public static  final String [] DEFAULT_TYPES = {
            "Agression avec arme",
            "Vol",
            "Viol",
            "Panne de null part",
            "Accident",
            "Besoin d'un medcin",
            "Autre"
    };


    public static final String AVATAR = "https://www.gravatar.com/avatar/00000000000000000000000000000000?d=https%3A%2F%2Fexample.com%2Fimages%2Favatar.jpg";

    public static final float DEFAULT_ZOOM_MAP = 15f;
    public static final String LAT = "lat";
    public static final String LONG = "long";
    public static final int RESQUEST_CODE_PLACE_PICKER =2 ;
    public static final String USER_ID_DEST = "UserdId";
    public static final String USER_PROFILE_DEST = "user profile dest";
    public static final String USERNAME_DEST = "username dest ";
    public static final int MSG_TYPE_RECEIVE = 0;
    public static final int MSG_TYPE_SEND =1 ;
    public static final String CHAT_FRAGMENT = "Chat Fragment";
    public static final String MEMBERS_FRAGMENT = "Members Fragment";
    public static final String CURRENT_USER_IMAGE_PROFILE = "current user image profile";

    public static final String USERNAME_IMAGE = "image";
    public static final String DESC_DATA = "description";
    public static final String USERNAME ="username";
    public static final String DATE ="date" ;
    public static final String IMAGE_POST = "image post";
    public static final String BLOG_POST_ID = "id post ";
    public static String TYPE = "type";

    public static class Notification{
        public static final int LARGE_ICONE_SIZE = 256; //px
    }

    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String [] PERMISSIONS = {FINE_LOCATION,COARSE_LOCATION};

}
