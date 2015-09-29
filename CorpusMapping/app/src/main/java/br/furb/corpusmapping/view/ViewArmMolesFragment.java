package br.furb.corpusmapping.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RadioButton;

import br.furb.corpusmapping.capture.BodyImageSliderActivity;
import br.furb.corpusmapping.R;
import br.furb.corpusmapping.SpecificBodyPart;
import br.furb.corpusmapping.capture.SelectBodyPartActivity;
import br.furb.corpusmapping.util.ImageDrawer;

import static br.furb.corpusmapping.capture.BodyImageSliderActivity.PARAM_BODY_PARTS;
import static br.furb.corpusmapping.capture.BodyImageSliderActivity.PARAM_IMAGES;

public class ViewArmMolesFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ImageView imgArmLeftTop;
    private ImageView imgArmLeftDown;
    private ImageView imgArmRightDown;
    private ImageView imgArmRightTop;
    private RadioButton rbLeft;
    private RadioButton rbRight;
    private GridLayout pnLeft;
    private GridLayout pnRight;
    private View view;

    public static ViewArmMolesFragment newInstance() {
        ViewArmMolesFragment fragment = new ViewArmMolesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ViewArmMolesFragment() {
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
        view = inflater.inflate(R.layout.fragment_view_arm_moles, container, false);

        imgArmLeftDown = (ImageView) view.findViewById(R.id.imgArmLeftDown);
        imgArmLeftTop = (ImageView) view.findViewById(R.id.imgArmLeftTop);

        imgArmLeftDown.setOnClickListener(this);
        imgArmLeftTop.setOnClickListener(this);

        ImageDrawer.drawPoints(imgArmLeftDown, SpecificBodyPart.LEFT_ARM_DOWN, R.drawable.braco_esquerdo_baixo);
        ImageDrawer.drawPoints(imgArmLeftTop, SpecificBodyPart.LEFT_ARM_TOP, R.drawable.braco_esquerdo_cima);

        imgArmRightDown = (ImageView) view.findViewById(R.id.imgArmRightDown);
        imgArmRightTop = (ImageView) view.findViewById(R.id.imgArmRightTop);

        imgArmRightDown.setOnClickListener(this);
        imgArmRightTop.setOnClickListener(this);

        ImageDrawer.drawPoints(imgArmRightDown, SpecificBodyPart.RIGHT_ARM_DOWN, R.drawable.braco_direito_baixo);
        ImageDrawer.drawPoints(imgArmRightTop, SpecificBodyPart.RIGHT_ARM_TOP, R.drawable.braco_direito_cima);


        rbRight = (RadioButton) view.findViewById(R.id.rbRight);
        rbLeft = (RadioButton) view.findViewById(R.id.rbLeft);

        pnLeft = (GridLayout) view.findViewById(R.id.pnLeft);
        pnRight = (GridLayout) view.findViewById(R.id.pnRight);

        rbLeft.setChecked(true);

        rbLeft.setOnCheckedChangeListener(this);
        rbRight.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(getActivity(), BodyImageSliderActivity.class);
        i.putExtra(BodyImageSliderActivity.PARAM_IS_TO_ASSOCIATE, false);
        switch (v.getId()) {
            case R.id.imgArmLeftDown:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.braco_esquerdo_baixo, R.drawable.braco_esquerdo_cima});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.LEFT_ARM_DOWN.name(), SpecificBodyPart.LEFT_ARM_TOP.name()});
                break;
            case R.id.imgArmLeftTop:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.braco_esquerdo_cima, R.drawable.braco_esquerdo_baixo});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.LEFT_ARM_TOP.name(), SpecificBodyPart.LEFT_ARM_DOWN.name()});
                break;
            case R.id.imgArmRightDown:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.braco_direito_baixo, R.drawable.braco_direito_cima});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.RIGHT_ARM_DOWN.name(), SpecificBodyPart.RIGHT_ARM_TOP.name()});
                break;
            case R.id.imgArmRightTop:
                i.putExtra(PARAM_IMAGES, new int[]{R.drawable.braco_direito_cima, R.drawable.braco_direito_baixo});
                i.putExtra(PARAM_BODY_PARTS, new String[]{SpecificBodyPart.RIGHT_ARM_TOP.name(), SpecificBodyPart.RIGHT_ARM_DOWN.name()});
                break;
        }
        getActivity().startActivityForResult(i, SelectBodyPartActivity.REQUEST_CODE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView.getId() == R.id.rbLeft) {
            if (rbLeft.isChecked()) {
                rbRight.setChecked(false);
                pnLeft.setVisibility(View.VISIBLE);
                pnRight.setVisibility(View.INVISIBLE);
            }
        } else {
            if (rbRight.isChecked()) {
                rbLeft.setChecked(false);
                pnRight.setVisibility(View.VISIBLE);
                pnLeft.setVisibility(View.INVISIBLE);
            }
        }
    }
}
