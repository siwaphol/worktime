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
package eu.vranckaert.worktime.activities.reporting;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.inject.Inject;
import com.google.inject.internal.Nullable;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.comparators.project.ProjectByNameComparator;
import eu.vranckaert.worktime.comparators.task.TaskByNameComparator;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.constants.TrackerConstants;
import eu.vranckaert.worktime.enums.reporting.ReportingDataGrouping;
import eu.vranckaert.worktime.enums.reporting.ReportingDataOrder;
import eu.vranckaert.worktime.enums.reporting.ReportingDateRange;
import eu.vranckaert.worktime.enums.reporting.ReportingDisplayDuration;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.service.ExportService;
import eu.vranckaert.worktime.service.ProjectService;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.date.DateConstants;
import eu.vranckaert.worktime.utils.date.DateFormat;
import eu.vranckaert.worktime.utils.date.DateUtils;
import eu.vranckaert.worktime.utils.file.CsvFilenameFilter;
import eu.vranckaert.worktime.utils.string.StringUtils;
import eu.vranckaert.worktime.utils.tracker.AnalyticsTracker;
import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

import java.io.File;
import java.util.*;

/**
 * User: DIRK VRANCKAERT
 * Date: 15/09/11
 * Time: 20:28
 */
public class ReportingCriteriaActivity extends GuiceActivity {
    private static final String LOG_TAG = ReportingCriteriaActivity.class.getSimpleName();

    private List<ReportingDateRange> dateRanges;
    private List<ReportingDataGrouping> dataGroupings;
    private List<ReportingDisplayDuration> displayDurations;
    private List<ReportingDataOrder> dataOrders;

    @InjectExtra(value = Constants.Extras.TIME_REGISTRATION_START_DATE, optional = true)
    @Nullable
    private Date startDate;
    @InjectExtra(value = Constants.Extras.TIME_REGISTRATION_END_DATE, optional = true)
    @Nullable
    private Date endDate;
    @InjectExtra(value = Constants.Extras.PROJECT, optional = true)
    @Nullable
    private Project project;
    @InjectExtra(value = Constants.Extras.TASK, optional = true)
    @Nullable
    private Task task;
    @InjectExtra(value = Constants.Extras.DATA_GROUPING, optional = true)
    @Nullable
    private ReportingDataGrouping dataGrouping;
    @InjectExtra(value = Constants.Extras.DATA_ORDER, optional = true)
    @Nullable
    private ReportingDataOrder dataOrder;
    @InjectExtra(value = Constants.Extras.DISPLAY_DURATION, optional = true)
    @Nullable
    private ReportingDisplayDuration displayDuration;

    private List<Project> availableProjects = null;
    private List<Task> availableTasks = null;

    @InjectView(R.id.btn_action_export)
    private View actionExportButton;
    @InjectView(R.id.reporting_criteria_date_range_spinner)
    private Spinner dateRangeSpinner;
    @InjectView(R.id.reporting_criteria_data_grouping_spinner)
    private Spinner dataGroupingSpinner;
    @InjectView(R.id.reporting_criteria_data_display_duration_spinner)
    private Spinner displayDurationSpinner;
    @InjectView(R.id.reporting_criteria_data_order_spinner)
    private Spinner dataOrderSpinner;
    @InjectView(R.id.reporting_criteria_date_range_start)
    private Button dateRangeStartButton;
    @InjectView(R.id.reporting_criteria_date_range_end)
    private Button dateRangeEndButton;
    @InjectView(R.id.reporting_criteria_project)
    private Button projectButton;
    @InjectView(R.id.reporting_criteria_task)
    private Button taskButton;
    @InjectView(R.id.reporting_criteria_show_finished_tasks)
    private CheckBox showFinishedTasks;
    @InjectView(R.id.btn_delete_project)
    private ImageView deleteProjectButton;
    @InjectView(R.id.btn_delete_task)
    private ImageView deleteTaskButton;

    @Inject
    private ProjectService projectService;
    @Inject
    private TaskService taskService;
    @Inject
    private ExportService exportService;

    private AnalyticsTracker tracker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporting_criteria);
        tracker = AnalyticsTracker.getInstance(getApplicationContext());
        tracker.trackPageView(TrackerConstants.PageView.REPORTING_CRITERIA_ACTIVITY);

        initializeView();
    }

    private void initializeView() {
        //Export button
        checkForEnablingBatchSharing();

        //Date Range spinner
        dateRanges = Arrays.asList(ReportingDateRange.values());
        Collections.sort(dateRanges, new Comparator<ReportingDateRange>() {
            public int compare(ReportingDateRange reportingDateRange, ReportingDateRange reportingDateRange1) {
                return ((Integer)reportingDateRange.getOrder()).compareTo((Integer)reportingDateRange1.getOrder());
            }
        });
        ArrayAdapter<CharSequence> dateRangeAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_reporting_criteria_date_range_spinner, android.R.layout.simple_spinner_item);
        dateRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateRangeSpinner.setAdapter(dateRangeAdapter);
        dateRangeSpinner.setSelection(ReportingDateRange.TODAY.getOrder()); //Set default value...

        dateRangeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                updateViewOnDateRangeSpinnerSelection();
            }
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //Date Range start button
        dateRangeStartButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDialog(Constants.Dialog.REPORTING_CRITERIA_SELECT_START_DATE);
            }
        });

        //Date Range end button
        dateRangeEndButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDialog(Constants.Dialog.REPORTING_CRITERIA_SELECT_END_DATE);
            }
        });

        //Project select button
        projectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDialog(Constants.Dialog.REPORTING_CRITERIA_SELECT_PROJECT);
            }
        });
        //Project delete button
        deleteProjectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                project = null;
                updateViewOnProjectAndTaskSelection();
            }
        });

        //Task select button
        taskButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDialog(Constants.Dialog.REPORTING_CRITERIA_SELECT_TASK);
            }
        });
        //Task delete button
        deleteTaskButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                task = null;
                updateViewOnProjectAndTaskSelection();
            }
        });

        //Data Grouping spinner
        dataGroupings = Arrays.asList(ReportingDataGrouping.values());
        Collections.sort(dataGroupings, new Comparator<ReportingDataGrouping>() {
            public int compare(ReportingDataGrouping reportingDataGrouping, ReportingDataGrouping reportingDataGrouping1) {
                return ((Integer) reportingDataGrouping.getOrder()).compareTo((Integer) reportingDataGrouping1.getOrder());
            }
        });
        ArrayAdapter<CharSequence> dataGroupingAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_reporting_criteria_data_grouping_spinner, android.R.layout.simple_spinner_item);
        dataGroupingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataGroupingSpinner.setAdapter(dataGroupingAdapter);
        dataGroupingSpinner.setSelection(ReportingDataGrouping.GROUPED_BY_START_DATE.getOrder()); //Set default value...
        this.dataGrouping = ReportingDataGrouping.GROUPED_BY_START_DATE;

        dataGroupingSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                ReportingDataGrouping[] dataGroupings = ReportingDataGrouping.values();
                for (ReportingDataGrouping dataGrouping : dataGroupings) {
                    if (dataGrouping.getOrder() == pos) {
                        ReportingCriteriaActivity.this.dataGrouping = dataGrouping;
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //Data Order spinner
        dataOrders = Arrays.asList(ReportingDataOrder.values());
        Collections.sort(dataOrders, new Comparator<ReportingDataOrder>() {
            public int compare(ReportingDataOrder reportingDataOrder, ReportingDataOrder reportingDataOrder1) {
                return ((Integer) reportingDataOrder.getOrder()).compareTo((Integer) reportingDataOrder1.getOrder());
            }
        });
        ArrayAdapter<CharSequence> dataOrderAdapter = ArrayAdapter.createFromResource(this,
                R.array.lbl_reporting_criteria_data_order_spinner, android.R.layout.simple_spinner_item);
        dataOrderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataOrderSpinner.setAdapter(dataOrderAdapter);
        dataOrderSpinner.setSelection(ReportingDataOrder.DESC.getOrder()); //Set default value...
        this.dataOrder = ReportingDataOrder.DESC;

        dataOrderSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                ReportingDataOrder[] dataOrders = ReportingDataOrder.values();
                for (ReportingDataOrder dataOrder : dataOrders) {
                    if (dataOrder.getOrder() == pos) {
                        ReportingCriteriaActivity.this.dataOrder = dataOrder;
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //Display Duration spinner
        displayDurations = Arrays.asList(ReportingDisplayDuration.values());
        Collections.sort(displayDurations, new Comparator<ReportingDisplayDuration>() {
            public int compare(ReportingDisplayDuration reportingDisplayDuration, ReportingDisplayDuration reportingDisplayDuration1) {
                return ((Integer) reportingDisplayDuration.getOrder()).compareTo((Integer) reportingDisplayDuration1.getOrder());
            }
        });
        ArrayAdapter<CharSequence> displayDurationAdapater = ArrayAdapter.createFromResource(this,
                R.array.array_reporting_criteria_data_display_duration_spinner, android.R.layout.simple_spinner_item);

        displayDurationAdapater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        displayDurationSpinner.setAdapter(displayDurationAdapater);
        displayDurationSpinner.setSelection(ReportingDisplayDuration.HOUR_MINUTES_SECONDS.getOrder()); //Set default value...
        this.displayDuration = ReportingDisplayDuration.HOUR_MINUTES_SECONDS;

        displayDurationSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                ReportingDisplayDuration[] displayDurations = ReportingDisplayDuration.values();
                for (ReportingDisplayDuration displayDuration : displayDurations) {
                    if (displayDuration.getOrder() == pos) {
                        ReportingCriteriaActivity.this.displayDuration = displayDuration;
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //Handle changes...
        updateViewOnDateRangeSpinnerSelection();
        updateViewOnProjectAndTaskSelection();
    }

    private void checkForEnablingBatchSharing() {
        File documentDirectory = exportService.getDocumentDirectory();
        File[] documents = documentDirectory.listFiles(new CsvFilenameFilter());

        if (documents != null && documents.length > 0) {
            actionExportButton.setVisibility(View.VISIBLE);
        } else {
            actionExportButton.setVisibility(View.INVISIBLE);
        }
    }

    private void updateViewOnDateRangeSpinnerSelection() {
        int index = dateRangeSpinner.getSelectedItemPosition();

        if (ReportingDateRange.TODAY.getOrder() == index) {
            Date today  = new Date();
            startDate = today;
            endDate = today;
            String todayAsText = DateUtils.convertDateToString(startDate, DateFormat.LONG, this);

            dateRangeStartButton.setText(todayAsText);
            dateRangeEndButton.setText(todayAsText);

            dateRangeStartButton.setEnabled(false);
            dateRangeEndButton.setEnabled(false);
        } else if (ReportingDateRange.THIS_WEEK.getOrder() == index) {
            Map<Integer, Date> result = DateUtils.calculateWeekBoundaries(0, ReportingCriteriaActivity.this);
            Date firstDay = result.get(DateConstants.FIRST_DAY_OF_WEEK);
            Date lastDay = result.get(DateConstants.LAST_DAY_OF_WEEK);

            startDate = firstDay;
            endDate = lastDay;

            String strFirstDay = DateUtils.convertDateToString(firstDay, DateFormat.LONG, this);
            String strLastDay = DateUtils.convertDateToString(lastDay, DateFormat.LONG, this);

            dateRangeStartButton.setText(strFirstDay);
            dateRangeEndButton.setText(strLastDay);

            dateRangeStartButton.setEnabled(false);
            dateRangeEndButton.setEnabled(false);
        } else if (ReportingDateRange.LAST_WEEK.getOrder() == index) {
            Map<Integer, Date> result = DateUtils.calculateWeekBoundaries(-1, ReportingCriteriaActivity.this);
            Date firstDay = result.get(DateConstants.FIRST_DAY_OF_WEEK);
            Date lastDay = result.get(DateConstants.LAST_DAY_OF_WEEK);

            startDate = firstDay;
            endDate = lastDay;

            String strFirstDay = DateUtils.convertDateToString(firstDay, DateFormat.LONG, this);
            String strLastDay = DateUtils.convertDateToString(lastDay, DateFormat.LONG, this);

            dateRangeStartButton.setText(strFirstDay);
            dateRangeEndButton.setText(strLastDay);

            dateRangeStartButton.setEnabled(false);
            dateRangeEndButton.setEnabled(false);
        } else if (ReportingDateRange.ALL_TIMES.getOrder() == index) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0L);
            cal.set(Calendar.YEAR, 1900);
            Date firstDay = cal.getTime();
            cal.set(Calendar.YEAR, 9999);
            Date lastDay = cal.getTime();

            startDate = firstDay;
            endDate = lastDay;

            String strFirstDay = DateUtils.convertDateToString(firstDay, DateFormat.LONG, this);
            String strLastDay = DateUtils.convertDateToString(lastDay, DateFormat.LONG, this);

            dateRangeStartButton.setText(strFirstDay);
            dateRangeEndButton.setText(strLastDay);

            dateRangeStartButton.setEnabled(false);
            dateRangeEndButton.setEnabled(false);
        } else if (ReportingDateRange.CUSTOM.getOrder() == index) {
            String strStartDate = DateUtils.convertDateToString(startDate, DateFormat.LONG, this);
            String strEndDate = DateUtils.convertDateToString(endDate, DateFormat.LONG, this);

            dateRangeStartButton.setText(strStartDate);
            dateRangeEndButton.setText(strEndDate);

            dateRangeStartButton.setEnabled(true);
            dateRangeEndButton.setEnabled(true);
        }
    }

    private void updateViewOnProjectAndTaskSelection() {
        if (project == null) {
            projectButton.setText(R.string.lbl_reporting_criteria_project_any);
            deleteProjectButton.setVisibility(View.GONE);

            taskButton.setText(R.string.lbl_reporting_criteria_task_any);
            taskButton.setEnabled(false);
            showFinishedTasks.setEnabled(false);
            task = null;
            deleteTaskButton.setVisibility(View.GONE);
        } else {
            projectButton.setText(project.getName());
            deleteProjectButton.setVisibility(View.VISIBLE);

            taskButton.setEnabled(true);
            showFinishedTasks.setEnabled(true);
            if (task == null) {
                taskButton.setText(R.string.lbl_reporting_criteria_task_any);
                deleteTaskButton.setVisibility(View.GONE);
            } else {
                taskButton.setText(task.getName());
                deleteTaskButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;

        switch (id) {
            case Constants.Dialog.REPORTING_CRITERIA_SELECT_PROJECT: {
                //Find all projects and sort by name
                availableProjects = projectService.findAll();
                Collections.sort(availableProjects, new ProjectByNameComparator());

                Project selectedProject = projectService.getSelectedProject();

                List<String> projects = new ArrayList<String>();
                for (int i=0; i<availableProjects.size(); i++) {
                    Project project = availableProjects.get(i);

                    projects.add(project.getName());
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.lbl_reporting_criteria_project_dialog_title_select_project)
                       .setSingleChoiceItems(
                               StringUtils.convertListToArray(projects),
                               -1,
                               new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int index) {
                                        project = availableProjects.get(index);
                                        task = null;
                                        updateViewOnProjectAndTaskSelection();
                                        removeDialog(Constants.Dialog.REPORTING_CRITERIA_SELECT_PROJECT);
                                    }
                               }
                       )
                       .setOnCancelListener(new DialogInterface.OnCancelListener() {
                           public void onCancel(DialogInterface dialogInterface) {
                               removeDialog(Constants.Dialog.REPORTING_CRITERIA_SELECT_PROJECT);
                           }
                       });
                dialog = builder.create();
                break;
            }
            case Constants.Dialog.REPORTING_CRITERIA_SELECT_TASK: {
                if (showFinishedTasks.isChecked()) {
                    availableTasks = taskService.findTasksForProject(project);
                } else {
                    availableTasks = taskService.findNotFinishedTasksForProject(project);
                }
                Collections.sort(availableTasks, new TaskByNameComparator());

                List<String> tasks = new ArrayList<String>();
                for (Task task : availableTasks) {
                    tasks.add(task.getName());
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.lbl_widget_title_select_task)
                       .setSingleChoiceItems(
                               StringUtils.convertListToArray(tasks),
                               -1,
                               new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int index) {
                                        task = availableTasks.get(index);
                                        updateViewOnProjectAndTaskSelection();
                                        removeDialog(Constants.Dialog.REPORTING_CRITERIA_SELECT_TASK);
                                    }
                               }
                       )
                       .setOnCancelListener(new DialogInterface.OnCancelListener() {
                           public void onCancel(DialogInterface dialogInterface) {
                               removeDialog(Constants.Dialog.REPORTING_CRITERIA_SELECT_TASK);
                           }
                       });
                dialog = builder.create();
                break;
            }
            case Constants.Dialog.REPORTING_CRITERIA_SELECT_START_DATE: {
                final Calendar startDateCal = Calendar.getInstance();
                startDateCal.setTime(startDate);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ReportingCriteriaActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datePickerView
                                    , int year, int monthOfYear, int dayOfMonth) {
                                startDateCal.set(Calendar.YEAR, year);
                                startDateCal.set(Calendar.MONTH, monthOfYear);
                                startDateCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                startDate = startDateCal.getTime();
                                updateViewOnDateRangeSpinnerSelection();

                                if (startDate.after(endDate)) {
                                    showDialog(Constants.Dialog.REPORTING_CRITERIA_SELECT_END_DATE);
                                }

                                removeDialog(Constants.Dialog.REPORTING_CRITERIA_SELECT_START_DATE);
                            }
                        },
                        startDateCal.get(Calendar.YEAR),
                        startDateCal.get(Calendar.MONTH),
                        startDateCal.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.setTitle(R.string.lbl_reporting_criteria_date_from_picker);
                datePickerDialog.setButton2(getString(android.R.string.cancel), new DatePickerDialog.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeDialog(Constants.Dialog.REPORTING_CRITERIA_SELECT_START_DATE);
                    }
                });
                dialog = datePickerDialog;
                break;
            }
            case Constants.Dialog.REPORTING_CRITERIA_SELECT_END_DATE: {
                final Calendar endDateCal = Calendar.getInstance();
                endDateCal.setTime(endDate);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ReportingCriteriaActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datePickerView
                                    , int year, int monthOfYear, int dayOfMonth) {
                                endDateCal.set(Calendar.YEAR, year);
                                endDateCal.set(Calendar.MONTH, monthOfYear);
                                endDateCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                endDate = endDateCal.getTime();
                                updateViewOnDateRangeSpinnerSelection();

                                if (endDate.before(startDate)) {
                                    showDialog(Constants.Dialog.REPORTING_CRITERIA_SELECT_END_DATE_ERROR_BEFORE_START_DATE);
                                } else {
                                    removeDialog(Constants.Dialog.REPORTING_CRITERIA_SELECT_END_DATE);
                                }
                            }
                        },
                        endDateCal.get(Calendar.YEAR),
                        endDateCal.get(Calendar.MONTH),
                        endDateCal.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.setTitle(R.string.lbl_reporting_criteria_date_till_picker);
                datePickerDialog.setButton2(getString(android.R.string.cancel), new DatePickerDialog.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeDialog(Constants.Dialog.REPORTING_CRITERIA_SELECT_START_DATE);
                    }
                });

                dialog = datePickerDialog;
                break;
            }
            case Constants.Dialog.REPORTING_CRITERIA_SELECT_END_DATE_ERROR_BEFORE_START_DATE: {
                String startDateString = DateUtils.convertDateToString(startDate, DateFormat.LONG, this);
                AlertDialog.Builder alertValidationError = new AlertDialog.Builder(this);
				alertValidationError
                           .setTitle(R.string.lbl_reporting_criteria_date_till_picker_validation_error_title)
						   .setMessage( getString(
                                   R.string.msg_reporting_criteria_date_till_picker_validation_error_before_start_date,
                                   startDateString
                           ))
						   .setCancelable(false)
						   .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   dialog.cancel();
                                   showDialog(Constants.Dialog.REPORTING_CRITERIA_SELECT_END_DATE);
                               }
                           });
				dialog = alertValidationError.create();
                break;
            }
            case Constants.Dialog.REPORTING_BATCH_SHARE: {
                final List<File> selectedDocuments = new ArrayList<File>();
                final File[] documents = exportService.getDocumentDirectory().listFiles(new CsvFilenameFilter());

                String[] documentNames = new String[documents.length];
                boolean[] checkedNames = new boolean[documents.length];
                for (int i=0; i<documents.length; i++) {
                    File document = documents[i];
                    documentNames[i] = document.getName();
                    checkedNames[i] = Boolean.FALSE;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.lbl_reporting_criteria_batch_share_dialog_title)
                       .setMultiChoiceItems(documentNames,
                               checkedNames,
                               new DialogInterface.OnMultiChoiceClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                       Log.d(LOG_TAG, (isChecked?"Selecting":"Unselecting") + " document on position " + which + " with title '" + documents[which].getName() + "'");
                                       if (isChecked) {
                                           selectedDocuments.add(documents[which]);
                                       } else {
                                           selectedDocuments.remove(documents[which]);
                                       }
                                   }
                               }
                       )
                       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               Log.d(LOG_TAG, "Continuing to batch share with " + selectedDocuments.size() + " document(s)");
                               if (selectedDocuments.size() == 0) {
                                   Log.d(LOG_TAG, "Showing toast message notifying the user he must select a document to share");
                                   Toast.makeText(ReportingCriteriaActivity.this, R.string.msg_reporting_criteria_batch_share_no_docs_selected,  Toast.LENGTH_SHORT).show();
                                   return;
                               }
                               removeDialog(Constants.Dialog.REPORTING_BATCH_SHARE);
                               batchShareDocuments(selectedDocuments);
                           }
                       })
                       .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               Log.d(LOG_TAG, "Stopping the batch share procedure using the cancel button");
                               removeDialog(Constants.Dialog.REPORTING_BATCH_SHARE);
                           }
                       })
                       .setOnCancelListener(new DialogInterface.OnCancelListener() {
                           public void onCancel(DialogInterface dialogInterface) {
                               Log.d(LOG_TAG, "Stopping the batch share procedure using the device back");
                               removeDialog(Constants.Dialog.REPORTING_BATCH_SHARE);
                           }
                       });
                dialog = builder.create();
                break;
            }
        }

        return dialog;
    }

    private void batchShareDocuments(List<File> selectedDocuments) {
        Log.d(LOG_TAG, "Sharing " + selectedDocuments.size() + " document(s)");

        IntentUtil.sendSomething(
                ReportingCriteriaActivity.this,
                R.string.lbl_reporting_criteria_batch_share_subject,
                R.string.lbl_reporting_criteria_batch_share_body,
                selectedDocuments,
                R.string.lbl_reporting_criteria_batch_share_app_chooser_title
        );
    }

    public void onGenerateReportClick(View view) {
        if (startDate.after(endDate)) {
            showDialog(Constants.Dialog.REPORTING_CRITERIA_SELECT_END_DATE_ERROR_BEFORE_START_DATE);
            return;
        }

        tracker.trackEvent(
                TrackerConstants.EventSources.REPORTING_CRITERIA_ACTIVITY,
                TrackerConstants.EventActions.GENERATE_REPORT
        );

        Intent intent = new Intent(ReportingCriteriaActivity.this, ReportingResultActivity.class);
        intent.putExtra(Constants.Extras.TIME_REGISTRATION_START_DATE, startDate);
        intent.putExtra(Constants.Extras.TIME_REGISTRATION_END_DATE, endDate);
        intent.putExtra(Constants.Extras.PROJECT, project);
        intent.putExtra(Constants.Extras.TASK, task);
        intent.putExtra(Constants.Extras.DATA_GROUPING, dataGrouping);
        intent.putExtra(Constants.Extras.DATA_ORDER, dataOrder);
        intent.putExtra(Constants.Extras.DISPLAY_DURATION, displayDuration);
        startActivity(intent);
    }

    public void onBatchShareClick(View view) {
        showDialog(Constants.Dialog.REPORTING_BATCH_SHARE);
    }

    public void onHomeClick(View view) {
        IntentUtil.goHome(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tracker.stopSession();
    }

    @Override
    protected void onRestart() {
        Log.d(LOG_TAG, "Checking if batch sharing should be enabled when coming back...");
        super.onRestart();
        checkForEnablingBatchSharing();
    }
}