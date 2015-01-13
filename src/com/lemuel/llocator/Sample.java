package com.lemuel.llocator;

import volleyutil.RequestListener;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

@SuppressLint("NewApi") 
public class Sample extends Fragment{
    
    private Button button;
    private RequestListener req;
    
    public Sample (RequestListener req){
        this.req = req;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sample, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        button = (Button)getActivity().findViewById(R.id.button1);
        button.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
               req.onPress();
            }
        });
       
    }
    
    

}
