package edu.umb.cs443;

        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import androidx.annotation.Nullable;
        import androidx.fragment.app.Fragment;

public class SearchFragment extends Fragment {

    public SearchFragment() {}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle s){
        return inflater.inflate(R.layout.activity_research, container, false);
    }



}

