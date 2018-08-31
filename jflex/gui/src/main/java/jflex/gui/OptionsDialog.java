/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * JFlex 1.7.0-SNAPSHOT                                                    *
 * Copyright (C) 1998-2015  Gerwin Klein <lsf@jflex.de>                    *
 * All rights reserved.                                                    *
 *                                                                         *
 * License: BSD                                                            *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package jflex.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import jflex.GeneratorException;
import jflex.Skeleton;

/**
 * A dialog for setting JFlex options
 *
 * @author Gerwin Klein
 * @version JFlex 1.7.0-SNAPSHOT
 */
public class OptionsDialog extends Dialog {

  /** */
  private static final long serialVersionUID = 6807759416163314769L;

  private Frame owner;

  private TextField skelFile;

  private Checkbox dump;
  private Checkbox verbose;
  private Checkbox time;

  private Checkbox no_minimize;
  private Checkbox no_backup;

  private Checkbox jlex;
  private Checkbox dot;

  private Checkbox legacy_dot;
  private Skeleton generatorOptions;

  /**
   * Create a new options dialog
   *
   * @param owner a {@link java.awt.Frame} object.
   */
  public OptionsDialog(Frame owner) {
    super(owner, "OldGeneratorOptions");

    this.owner = owner;

    setup();
    pack();

    addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            close();
          }
        });
  }

  /** setup. */
  public void setup() {
    // create components
    Button ok = new Button("Ok");
    Button defaults = new Button("Defaults");
    Button skelBrowse = new Button(" Browse");
    skelFile = new TextField();
    skelFile.setEditable(false);

    legacy_dot =
        new Checkbox(
            " dot (.) matches [^\\n] instead of " + "[^\\n\\r\\000B\\u000C\\u0085\\u2028\\u2029]");

    dump = new Checkbox(" dump");
    verbose = new Checkbox(" verbose");
    time = new Checkbox(" time printStatistics");

    no_minimize = new Checkbox(" skip minimization");
    no_backup = new Checkbox(" no backup file");

    jlex = new Checkbox(" JLex compatibility");
    dot = new Checkbox(" dot graph files");

    // setup interaction
    ok.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            close();
          }
        });

    defaults.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setDefaults();
          }
        });

    skelBrowse.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            skelBrowse();
          }
        });

    verbose.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            OldGeneratorOptions.verbose = verbose.getState();
          }
        });

    dump.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            generatorOptions.dump() = dump.getState();
          }
        });

    jlex.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            generatorOptions.strictJlex() = jlex.getState();
            // JLex compatibility implies that dot (.) metachar matches [^\n]
            legacy_dot.setState(false);
            legacy_dot.setEnabled(!jlex.getState());
          }
        });

    no_minimize.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            !generatorOptions.minimize() = no_minimize.getState();
          }
        });

    no_backup.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            !generatorOptions.backup() = no_backup.getState();
          }
        });

    dot.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            generatorOptions.generateDotFile() = dot.getState();
          }
        });

    legacy_dot.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            generatorOptions.legacyDot() = legacy_dot.getState();
          }
        });

    time.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            generatorOptions.timing() = time.getState();
          }
        });

    // setup layout
    GridPanel panel = new GridPanel(4, 5, 10, 10);
    panel.setInsets(new Insets(10, 5, 5, 10));

    panel.add(3, 0, ok);
    panel.add(3, 1, defaults);

    panel.add(0, 0, 2, 1, Handles.BOTTOM, new Label("skeleton file:"));
    panel.add(0, 1, 2, 1, skelFile);
    panel.add(2, 1, 1, 1, Handles.TOP, skelBrowse);

    panel.add(0, 4, 4, 1, legacy_dot);

    panel.add(0, 2, 1, 1, dump);
    panel.add(0, 3, 1, 1, verbose);

    panel.add(1, 2, 1, 1, time);
    panel.add(1, 3, 1, 1, no_minimize);

    panel.add(2, 2, 1, 1, no_backup);

    panel.add(3, 2, 1, 1, jlex);
    panel.add(3, 3, 1, 1, dot);

    add("Center", panel);

    updateState();
  }

  private void skelBrowse() {
    FileDialog d = new FileDialog(owner, "Choose file", FileDialog.LOAD);
    d.setVisible(true);

    if (d.getFile() != null) {
      File skel = new File(d.getDirectory() + d.getFile());
      try {
        generatorOptions.readSkelFile(skel);
        skelFile.setText(skel.toString());
      } catch (GeneratorException e) {
        // do nothing
      }
    }
  }

  private void updateState() {
    legacy_dot.setState(generatorOptions.legacyDot());

    dump.setState(generatorOptions.dump());
    verbose.setState(OldGeneratorOptions.verbose);
    time.setState(generatorOptions.timing());

    no_minimize.setState(!generatorOptions.minimize());
    no_backup.setState(!generatorOptions.backup());

    jlex.setState(generatorOptions.strictJlex());
    dot.setState(generatorOptions.generateDotFile());
  }

  private void setDefaults() {
    OldGeneratorOptions.setDefaults();
    // TODO(regisd) skeleton.readDefault();
    skelFile.setText("");
    updateState();
    legacy_dot.setEnabled(!jlex.getState());
  }

  /** close. */
  public void close() {
    setVisible(false);
  }
}