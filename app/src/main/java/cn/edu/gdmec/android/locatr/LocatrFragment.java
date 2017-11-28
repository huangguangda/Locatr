package cn.edu.gdmec.android.locatr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Jack on 2017/11/28.
 */

public class LocatrFragment extends Fragment{
    private ImageView mImageView;
    public static LocatrFragment newInstance(){
        return new LocatrFragment ();
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate ( savedInstanceState );
        setHasOptionsMenu ( true );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate ( R.layout.fragment_locatr, container, false );
        mImageView = (ImageView) v.findViewById ( R.id.image );
        return v;
    }
}
