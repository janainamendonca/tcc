package br.furb.corpusmapping.ui.bodylink;

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
import br.furb.corpusmapping.data.model.SpecificBodyPart;
import br.furb.corpusmapping.ui.common.BodyImageSliderActivity;
import br.furb.corpusmapping.util.ImageDrawer;

import static br.furb.corpusmapping.ui.common.BodyImageSliderActivity.PARAM_BODY_PARTS;
import static br.furb.corpusmapping.ui.common.BodyImageSliderActivity.PARAM_IMAGES;

/**
 * Fragment apresentado para o usuário selecionar a região (ponto) da cabeça onde deseja associar a imagem capturada.
 *  @author Janaina Carraro Mendonça Lima
 */
public class SelectionHeadFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    private ImageView imgHeadFront;
    private ImageView imgHeadBack;
    private ImageView imgHeadLeft;
    private ImageView imgHeadRight;

    public static SelectionHeadFragment newInstance() {
        SelectionHeadFragment fragment = new SelectionHeadFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SelectionHeadFragment() {
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
        View view = inflater.inflate(R.layout.fragment_selection_head, container, false);

        imgHeadFront = (ImageView) view.findViewById(R.id.imgHeadFront);
        imgHeadBack = (ImageView) view.findViewById(R.id.imgHeadBack);
        imgHeadLeft = (ImageView) view.findViewById(R.id.imgHeadLeft);
        imgHeadRight = (ImageView) view.findViewById(R.id.imgHeadRight);

        imgHeadFront.setOnClickListener(this);
        imgHeadBack.setOnClickListener(this);
        imgHeadLeft.setOnClickListener(this);
        imgHeadRight.setOnClickListener(this);

        ImageDrawer.drawPointsOfBodyPart(imgHeadFront, SpecificBodyPart.HEAD_FRONT, R.drawable.cabeca_frente);
        ImageDrawer.drawPointsOfBodyPart(imgHeadBack, SpecificBodyPart.HEAD_BACK, R.drawable.cabeca_atras);
        ImageDrawer.drawPointsOfBodyPart(imgHeadLeft, SpecificBodyPart.HEAD_LEFT, R.drawable.cabeca_esquerda);
        ImageDrawer.drawPointsOfBodyPart(imgHeadRight, SpecificBodyPart.HEAD_RIGHT, R.drawable.cabeca_direita);

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
            case R.id.imgHeadFront:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.cabeca_frente, R.drawable.cabeca_atras, R.drawable.cabeca_direita, R.drawable.cabeca_esquerda});

                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.HEAD_FRONT.name(), SpecificBodyPart.HEAD_BACK.name(), SpecificBodyPart.HEAD_RIGHT.name(), SpecificBodyPart.HEAD_LEFT.name()});
                break;
            case R.id.imgHeadBack:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.cabeca_atras, R.drawable.cabeca_frente, R.drawable.cabeca_direita, R.drawable.cabeca_esquerda});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.HEAD_BACK.name(), SpecificBodyPart.HEAD_FRONT.name(), SpecificBodyPart.HEAD_RIGHT.name(), SpecificBodyPart.HEAD_LEFT.name()});
                break;
            case R.id.imgHeadLeft:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.cabeca_esquerda, R.drawable.cabeca_direita, R.drawable.cabeca_frente, R.drawable.cabeca_atras});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.HEAD_LEFT.name(), SpecificBodyPart.HEAD_RIGHT.name(), SpecificBodyPart.HEAD_FRONT.name(), SpecificBodyPart.HEAD_BACK.name()});
                break;
            case R.id.imgHeadRight:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.cabeca_direita, R.drawable.cabeca_esquerda, R.drawable.cabeca_frente, R.drawable.cabeca_atras});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.HEAD_RIGHT.name(), SpecificBodyPart.HEAD_LEFT.name(), SpecificBodyPart.HEAD_FRONT.name(), SpecificBodyPart.HEAD_BACK.name()});
                break;
        }
        getActivity().startActivityForResult(i, SelectBodyPartActivity.REQUEST_CODE);
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }


}
