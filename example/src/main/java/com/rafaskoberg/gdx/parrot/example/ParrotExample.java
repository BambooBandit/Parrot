package com.rafaskoberg.gdx.parrot.example;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.rafaskoberg.gdx.parrot.Parrot;
import com.rafaskoberg.gdx.parrot.example.widgets.AmbiencePlayerWidget;
import com.rafaskoberg.gdx.parrot.example.widgets.MusicPlayerWidget;

public class ParrotExample extends ApplicationAdapter {
    private Parrot        parrot;
    private Stage         stage;
    private SpriteBatch   batch;
    private ParrotHandler parrotHandler;

    @Override
    public void create() {
        // Load Parrot
        parrot = new Parrot();

        // Load VisUI
        VisUI.load(Gdx.files.internal("skin/tinted.json"));

        // Create batch and stage
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        // Create ParrotHandler
        this.parrotHandler = new ParrotHandler(parrot);

        // Load sounds
        for(SoundType soundType : SoundType.values()) {
            AudioLoader.load(soundType);
        }

        // Load music
        for(MusicType musicType : MusicType.values()) {
            AudioLoader.load(musicType);
        }

        // Load ambience
        for(AmbienceType ambienceType : AmbienceType.values()) {
            AudioLoader.load(ambienceType);
        }

        // Create UI
        createUi();
    }

    private void createUi() {
        // Create root table
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        this.stage.addActor(rootTable);

        // Create buttons
        TextButton buttonFootsteps = new TextButton("Footsteps", VisUI.getSkin(), "toggle");
        buttonFootsteps.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parrotHandler.onFootstepButton(buttonFootsteps.isChecked());
            }
        });

        TextButton buttonWarning = new TextButton("Warning Beep", VisUI.getSkin(), "toggle");
        buttonWarning.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parrotHandler.onWarningButton(buttonWarning.isChecked());
            }
        });

        TextButton buttonFlamethrower = new TextButton("Flamethrower", VisUI.getSkin(), "toggle");
        buttonFlamethrower.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parrotHandler.onFlamethrowerButton(buttonFlamethrower.isChecked());
            }
        });

        // Create sliders
        VisSlider sliderSoundVolume = new VisSlider(0, 1, 0.01f, true);
        sliderSoundVolume.setValue(100);
        sliderSoundVolume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parrot.setSoundVolume(sliderSoundVolume.getValue());
            }
        });

        AmbiencePlayerWidget ambiencePlayerWidget = new AmbiencePlayerWidget(parrot);
        ambiencePlayerWidget.pack();

        MusicPlayerWidget musicPlayerWidget = new MusicPlayerWidget(parrot);
        musicPlayerWidget.pack();

        // Configure table
        rootTable.setFillParent(true);
        rootTable.add(ambiencePlayerWidget).colspan(3).growX().top();
        rootTable.row().uniform().expand().growX().space(40).center();
        rootTable.add(buttonFootsteps, buttonWarning, buttonFlamethrower);
        rootTable.row().uniform().expand().growX().space(40).center();
        rootTable.add(sliderSoundVolume);
        rootTable.row();
        rootTable.add(musicPlayerWidget).colspan(3).growX().bottom();
        rootTable.pack();
    }

    public void update(float delta) {
        stage.act(delta);
        parrotHandler.update(delta);
        parrot.updateSounds(0, 0, delta);
        parrot.updateMusic(delta);
    }

    @Override
    public void render() {
        update(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        VisUI.dispose();
        stage.dispose();
    }
}
