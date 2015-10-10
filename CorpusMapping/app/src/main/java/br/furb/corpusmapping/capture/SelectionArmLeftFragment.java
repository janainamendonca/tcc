package br.furb.corpusmapping.capture;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import br.furb.corpusmapping.R;
import br.furb.corpusmapping.SpecificBodyPart;
import br.furb.corpusmapping.util.ImageDrawer;

import static br.furb.corpusmapping.capture.BodyImageSliderActivity.PARAM_BODY_PARTS;
import static br.furb.corpusmapping.capture.BodyImageSliderActivity.PARAM_IMAGES;

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

        ImageDrawer.drawPoints(imgArmDown, SpecificBodyPart.LEFT_ARM_DOWN, R.drawable.braco_esquerdo_baixo);
        ImageDrawer.drawPoints(imgArmTop, SpecificBodyPart.LEFT_ARM_TOP, R.drawable.braco_esquerdo_cima);

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
            case R.id.imgArmLeftDown:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.braco_esquerdo_baixo, R.drawable.braco_esquerdo_cima});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.LEFT_ARM_DOWN.name(), SpecificBodyPart.LEFT_ARM_TOP.name()});
                break;
            case R.id.imgArmLeftTop:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.braco_esquerdo_cima, R.drawable.braco_esquerdo_baixo});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.LEFT_ARM_TOP.name(), SpecificBodyPart.LEFT_ARM_DOWN.name()});
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
