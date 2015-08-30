package br.furb.corpusmapping;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import static br.furb.corpusmapping.ImageSliderActivity.PARAM_IMAGES;

public class SelectionArmLeftFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    private ImageView imgArmTop;
    private ImageView imgArmDown;

    public static SelectionArmLeftFragment newInstance() {
        SelectionArmLeftFragment fragment = new SelectionArmLeftFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SelectionArmLeftFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selection_arm_left, container, false);

        imgArmDown = (ImageView) view.findViewById(R.id.imgArmLeftDown);
        imgArmTop = (ImageView) view.findViewById(R.id.imgArmLeftTop);

        imgArmDown.setOnClickListener(this);
        imgArmTop.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       /* try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(getActivity(), ImageSliderActivity.class);
        switch (v.getId()) {
            case R.id.imgArmLeftDown:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.braco_esquerdo_baixo, R.drawable.braco_esquerdo_cima});
                break;
            case R.id.imgArmLeftTop:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.braco_esquerdo_cima, R.drawable.braco_esquerdo_baixo});
                break;

        }
        getActivity().startActivity(i);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


}
