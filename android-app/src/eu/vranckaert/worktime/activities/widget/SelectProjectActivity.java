/*
 *  Copyright 2011 Dirk Vranckaert
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.vranckaert.worktime.activities.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.comparators.project.ProjectByNameComparator;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.service.ProjectService;
import eu.vranckaert.worktime.service.WidgetService;
import eu.vranckaert.worktime.utils.string.StringUtils;
import roboguice.activity.GuiceActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 02/03/11
 * Time: 20:56
 */
public class SelectProjectActivity extends GuiceActivity {
    @Inject
    private ProjectService projectService;

    @Inject
    private WidgetService widgetService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showDialog(Constants.Dialog.CHOOSE_SELECTED_PROJECT);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;

        switch (id) {
            case Constants.Dialog.CHOOSE_SELECTED_PROJECT: {
                //Find all projects and sort by name
                final List<Project> availableProjects = projectService.findUnfinishedProjects();
                Collections.sort(availableProjects, new ProjectByNameComparator());

                Project selectedProject = projectService.getSelectedProject();
                int selectedProjectIndex = -1;

                List<String> projects = new ArrayList<String>();
                for (int i=0; i<availableProjects.size(); i++) {
                    Project project = availableProjects.get(i);

                    if (selectedProject.getId() == project.getId()) {
                        selectedProjectIndex = i;
                    }

                    projects.add(project.getName());
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.lbl_widget_title_select_project)
                       .setSingleChoiceItems(
                               StringUtils.convertListToArray(projects),
                               selectedProjectIndex,
                               new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int index) {
                                        Project newSelectedProject = availableProjects.get(index);
                                        projectService.setSelectedProject(newSelectedProject);
                                        widgetService.updateWidget(SelectProjectActivity.this);
                                        SelectProjectActivity.this.finish();
                                    }
                               }
                       )
                       .setOnCancelListener(new DialogInterface.OnCancelListener() {
                           public void onCancel(DialogInterface dialogInterface) {
                               widgetService.updateWidget(SelectProjectActivity.this);
                               SelectProjectActivity.this.finish();
                           }
                       });
                dialog = builder.create();
                break;
            }
        }

        return dialog;
    }
}