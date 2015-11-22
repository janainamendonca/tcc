package br.furb.corpusmapping.ui.bodylink;

import android.content.Intent;
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
 * Fragment apresentado para o usuário selecionar a região (ponto) do braço com o qual deseja associar a imagem capturada.
 */
public class SelectionArmLeftFragment extends Fragment implements View.OnClickListener {

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

        ImageDrawer.drawPointsOfBodyPart(imgArmDown, SpecificBodyPart.LEFT_ARM_DOWN, R.drawable.braco_esquerdo_baixo);
        ImageDrawer.drawPointsOfBodyPart(imgArmTop, SpecificBodyPart.LEFT_ARM_TOP, R.drawable.braco_esquerdo_cima);

        return view;
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


}
