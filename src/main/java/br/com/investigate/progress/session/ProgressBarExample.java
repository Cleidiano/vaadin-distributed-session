package br.com.investigate.progress.session;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

public class ProgressBarExample extends CustomComponent {
    private static final long serialVersionUID = -4292553844521293140L;

    String context;

    static void into(VerticalLayout layout) {
        HorizontalLayout barbar = new HorizontalLayout();
        barbar.setMargin(true);
        barbar.setSizeFull();
        layout.addComponent(barbar);

        final ProgressBar progress = new ProgressBar(new Float(0.0));
        progress.setSizeFull();
        progress.setEnabled(false);
        barbar.addComponent(progress);

        final Label status = new Label("not running");
        barbar.addComponent(status);

        final Button button = new Button("Click to start");
        layout.addComponent(button);

        class WorkThread extends Thread {
            volatile double current = 0.0;

            @Override
            public void run() {
                while (current < 1.0) {
                    current += 0.01;

                    try {
                        sleep(50); // Sleep for 50 milliseconds
                    } catch (InterruptedException e) {
                    }

                    UI.getCurrent().access(() -> {
                        progress.setValue(new Float(current));
                        if (current < 1.0)
                            status.setValue("" +
                                    ((int) (current * 100)) + "% done");
                        else
                            status.setValue("all done");
                    });
                }

                try {
                    sleep(2000); // Sleep for 2 seconds
                } catch (InterruptedException e) {
                }

                UI.getCurrent().access(() -> {
                    progress.setValue(new Float(0.0));
                    progress.setEnabled(false);

                    UI.getCurrent().setPollInterval(-1);

                    button.setEnabled(true);
                    status.setValue("not running");
                });
            }
        }

        button.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = -1639461207460313184L;

            public void buttonClick(ClickEvent event) {
                final WorkThread thread = new WorkThread();
                thread.start();

                UI.getCurrent().setPollInterval(500);

                progress.setEnabled(true);
                button.setEnabled(false);

                status.setValue("running...");
            }
        });
    }

}