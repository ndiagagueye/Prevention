package com.example.gueye.memoireprevention2018.adaptaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gueye.memoireprevention2018.R;

//Cette classe va nous permettre de mieux gerer
// l'affichage de notre spinner (menu déroulant)

public class TypeUserAdaptater extends BaseAdapter{

    // déffinissons les champs

     String [] defaulltsTypes ;
     int [] defaultResources;
     LayoutInflater layoutInflater;

    //creeons un constructeur

    public TypeUserAdaptater(Context applicationContext , int [] defaultResources , String [] defaulltsTypes){

        this.defaulltsTypes =defaulltsTypes;
        this.defaultResources = defaultResources;
        layoutInflater = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return defaulltsTypes.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

       view = layoutInflater.inflate( R.layout.user_spinner_layout,null);

        ImageView ivDescription = (ImageView) view.findViewById( R.id.iv_user_type);
        TextView tvDescription = (TextView) view.findViewById( R.id.tv_user);

        ivDescription.setImageResource(defaultResources[i]);
        tvDescription.setText(defaulltsTypes[i]);
        return view;
    }
}
