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

import br.furb.corpusmapping.util.ImageDrawer;

import static br.furb.corpusmapping.BodyImageSliderActivity.PARAM_BODY_PARTS;
import static br.furb.corpusmapping.BodyImageSliderActivity.PARAM_IMAGES;

public class SelectionBodyFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    private ImageView imgBack;
    private ImageView imgFront;

    public static SelectionBodyFragment newInstance() {
        SelectionBodyFragment fragment = new SelectionBodyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SelectionBodyFragment() {
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
        View view = inflater.inflate(R.layout.fragment_selection_body, container, false);

        imgFront = (ImageView) view.findViewById(R.id.imgBodyFront);
        imgBack = (ImageView) view.findViewById(R.id.imgBodyBack);

        imgFront.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        ImageDrawer.drawPoints(imgFront, SpecificBodyPart.BODY_FRONT, R.drawable.tronco_frente);
        ImageDrawer.drawPoints(imgBack, SpecificBodyPart.BODY_BACK, R.drawable.tronco_costas);
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
        Intent i = new Intent(getActivity(), BodyImageSliderActivity.class);
        switch (v.getId()) {
            case R.id.imgBodyFront:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.tronco_frente, R.drawable.tronco_costas});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.BODY_FRONT.name(), SpecificBodyPart.BODY_BACK.name()});
                break;
            case R.id.imgBodyBack:
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.BODY_BACK.name(), SpecificBodyPart.BODY_FRONT.name()});
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.tronco_costas, R.drawable.tronco_frente});
                break;

        }
        getActivity().startActivityForResult(i, SelectBodyPartActivity.REQUEST_CODE);
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
