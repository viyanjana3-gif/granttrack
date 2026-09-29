// ============================================================
// GRANTTRACK - FRONTEND JAVASCRIPT
// Connects all frontend pages with Spring Boot REST APIs
// ============================================================


// ============================================================
// PAGE LOAD
// ============================================================

document.addEventListener("DOMContentLoaded", function () {

    console.log("GrantTrack JavaScript loaded");

    // Dashboard
    if (
        document.getElementById("totalApplications") ||
        document.getElementById("approvedApplications")
    ) {
        loadDashboard();
    }

    // Grant Stages page
    if (
        document.getElementById("applicationSelect") ||
        document.querySelector("#applicationId") ||
        document.querySelector(".stage-form")
    ) {
        loadApplicationsForStages();
        loadStages();
    }

    // Also detect stages page by URL
    if (window.location.pathname.includes("stages.html")) {
        loadApplicationsForStages();
        loadStages();
        setupStageForm();
    }

});


// ============================================================
// DASHBOARD
// ============================================================

async function loadDashboard() {

    try {

        const response = await fetch("/api/dashboard");

        if (!response.ok) {
            throw new Error("Failed to load dashboard data");
        }

        const data = await response.json();

        console.log("Dashboard data:", data);


        // Total Applications
        setText("totalApplications", data.totalApplications);

        // Approved Applications
        setText("approvedApplications", data.approvedApplications);

        // Rejected Applications
        setText("rejectedApplications", data.rejectedApplications);


        // Approved Grant
        setText(
            "totalApprovedGrant",
            formatCurrency(data.totalApprovedGrant)
        );

        setText(
            "grantAmount",
            formatCurrency(data.totalApprovedGrant)
        );


        // Expenditure
        setText(
            "expenditureAmount",
            formatCurrency(data.totalExpenditure)
        );


        // Remaining Grant
        setText(
            "remainingAmount",
            formatCurrency(data.totalRemainingGrant)
        );


        // Bottom overview
        setText(
            "applicationCount",
            data.totalApplications
        );

        setText(
            "approvedCount",
            data.approvedApplications
        );

        setText(
            "rejectedCount",
            data.rejectedApplications
        );


        // ====================================================
        // UTILIZATION
        // ====================================================

        let utilizationPercentage = 0;

        if (
            data.totalApprovedGrant &&
            data.totalApprovedGrant > 0
        ) {

            utilizationPercentage =
                (data.totalExpenditure /
                data.totalApprovedGrant) * 100;
        }

        // Progress bars
        const expenditureProgress =
            document.getElementById("expenditureProgress");

        if (expenditureProgress) {
            expenditureProgress.style.width =
                Math.min(utilizationPercentage, 100) + "%";
        }


        const grantProgress =
            document.getElementById("grantProgress");

        if (grantProgress) {
            grantProgress.style.width = "100%";
        }


        // Utilization text
        setText(
            "utilizationText",
            utilizationPercentage.toFixed(1) +
            "% utilized"
        );


    } catch (error) {

        console.error(
            "Dashboard loading error:",
            error
        );

        showDashboardError();
    }
}


// ============================================================
// GRANT STAGES
// ============================================================


// ------------------------------------------------------------
// Load Applications into Grant Stage dropdown
// ------------------------------------------------------------

async function loadApplicationsForStages() {

    try {

        const response =
            await fetch("/api/applications");

        if (!response.ok) {
            throw new Error(
                "Failed to load applications"
            );
        }

        const applications =
            await response.json();

        console.log(
            "Applications loaded for stages:",
            applications
        );


        // Find application dropdown
        const select =
            document.getElementById("applicationSelect") ||
            document.getElementById("applicationId") ||
            document.querySelector(
                'select[name="applicationId"]'
            );


        if (!select) {

            console.warn(
                "Application dropdown not found"
            );

            return;
        }


        // Clear existing options
        select.innerHTML = "";


        // Default option
        const defaultOption =
            document.createElement("option");

        defaultOption.value = "";

        defaultOption.textContent =
            "Select Application";

        select.appendChild(defaultOption);


        // Add applications
        applications.forEach(function (application) {

            const option =
                document.createElement("option");

            option.value = application.id;


            // Display title + ID
            option.textContent =
                "#" +
                application.id +
                " - " +
                application.title;


            select.appendChild(option);
        });


    } catch (error) {

        console.error(
            "Application loading error:",
            error
        );

        const select =
            document.getElementById("applicationSelect") ||
            document.getElementById("applicationId") ||
            document.querySelector(
                'select[name="applicationId"]'
            );

        if (select) {

            select.innerHTML =
                '<option value="">Unable to load applications</option>';
        }
    }
}


// ------------------------------------------------------------
// Load Grant Stage History
// ------------------------------------------------------------

async function loadStages() {

    try {

        const response =
            await fetch("/api/stages");

        if (!response.ok) {
            throw new Error(
                "Failed to load stages"
            );
        }

        const stages =
            await response.json();

        console.log(
            "Stages loaded:",
            stages
        );


        // Find table body
        const tableBody =
            document.getElementById("stagesTableBody") ||
            document.getElementById("stageTableBody") ||
            document.querySelector(
                "#stagesTable tbody"
            ) ||
            document.querySelector(
                "table tbody"
            );


        if (!tableBody) {

            console.warn(
                "Stage table body not found"
            );

            return;
        }


        // Clear old data
        tableBody.innerHTML = "";


        // No stages
        if (!stages || stages.length === 0) {

            tableBody.innerHTML = `
                <tr>
                    <td colspan="5"
                        style="text-align:center;">
                        No grant stages found.
                    </td>
                </tr>
            `;

            return;
        }


        // Add every stage
        stages.forEach(function (stage) {

            const row =
                document.createElement("tr");


            // ID
            const idCell =
                document.createElement("td");

            idCell.textContent =
                stage.id || "-";


            // Application
            const applicationCell =
                document.createElement("td");

            /*
             * Stage API currently returns only:
             * id, stageName, remarks, stageDate
             *
             * So application name may not be
             * directly available here.
             */

            if (stage.application) {

                applicationCell.textContent =
                    "#" +
                    stage.application.id +
                    " - " +
                    (
                        stage.application.title ||
                        "Application"
                    );

            } else {

                applicationCell.textContent =
                    "Application";
            }


            // Stage name
            const stageCell =
                document.createElement("td");

            stageCell.textContent =
                stage.stageName || "-";


            // Date
            const dateCell =
                document.createElement("td");

            dateCell.textContent =
                formatDate(stage.stageDate);


            // Remarks
            const remarksCell =
                document.createElement("td");

            remarksCell.textContent =
                stage.remarks || "-";


            // Add cells
            row.appendChild(idCell);
            row.appendChild(applicationCell);
            row.appendChild(stageCell);
            row.appendChild(dateCell);
            row.appendChild(remarksCell);


            // Add row
            tableBody.appendChild(row);

        });


    } catch (error) {

        console.error(
            "Stage loading error:",
            error
        );


        const tableBody =
            document.getElementById("stagesTableBody") ||
            document.getElementById("stageTableBody") ||
            document.querySelector(
                "#stagesTable tbody"
            ) ||
            document.querySelector(
                "table tbody"
            );


        if (tableBody) {

            tableBody.innerHTML = `
                <tr>
                    <td colspan="5"
                        style="text-align:center;">
                        Failed to load stages.
                    </td>
                </tr>
            `;
        }
    }
}


// ============================================================
// ADD GRANT STAGE
// ============================================================

function setupStageForm() {

    // Find form
    const form =
        document.getElementById("stageForm") ||
        document.querySelector("form");


    if (!form) {

        console.warn(
            "Stage form not found"
        );

        return;
    }


    // Prevent duplicate event listeners
    if (form.dataset.stageListener === "true") {
        return;
    }

    form.dataset.stageListener = "true";


    form.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();

            await addStage();
        }
    );
}


// ------------------------------------------------------------
// Add Stage
// ------------------------------------------------------------

async function addStage() {

    try {

        // Application
        const applicationSelect =
            document.getElementById("applicationSelect") ||
            document.getElementById("applicationId") ||
            document.querySelector(
                'select[name="applicationId"]'
            );


        // Stage
        const stageSelect =
            document.getElementById("stageSelect") ||
            document.querySelector(
                'select[name="stageName"]'
            );


        // Remarks
        const remarksInput =
            document.getElementById("remarks") ||
            document.querySelector(
                'textarea[name="remarks"]'
            );


        // Date
        const dateInput =
            document.getElementById("stageDate") ||
            document.querySelector(
                'input[name="stageDate"]'
            );


        // Validate
        if (
            !applicationSelect ||
            !applicationSelect.value
        ) {

            alert(
                "Please select an application."
            );

            return;
        }


        if (
            !stageSelect ||
            !stageSelect.value
        ) {

            alert(
                "Please select a stage."
            );

            return;
        }


        // Application ID
        const applicationId =
            applicationSelect.value;


        // Create request body
        const stageData = {

            stageName:
                stageSelect.value,

            remarks:
                remarksInput
                    ? remarksInput.value
                    : "",

            stageDate:
                dateInput
                    ? convertDateToISO(
                        dateInput.value
                    )
                    : null
        };


        console.log(
            "Sending stage:",
            stageData
        );


        // POST request
        const response =
            await fetch(
                "/api/stages/application/" +
                applicationId,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(stageData)
                }
            );


        if (!response.ok) {

            const errorText =
                await response.text();

            throw new Error(
                errorText ||
                "Failed to add stage"
            );
        }


        const result =
            await response.json();


        console.log(
            "Stage added:",
            result
        );


        alert(
            "Grant stage added successfully!"
        );


        // Reset form fields
        if (stageSelect) {
            stageSelect.value = "";
        }

        if (remarksInput) {
            remarksInput.value = "";
        }

        if (dateInput) {
            dateInput.value = "";
        }


        // Reload stage history
        await loadStages();


    } catch (error) {

        console.error(
            "Add stage error:",
            error
        );

        alert(
            "Failed to add grant stage.\n\n" +
            error.message
        );
    }
}


// ============================================================
// DATE FUNCTIONS
// ============================================================

function formatDate(date) {

    if (!date) {
        return "-";
    }

    // If already YYYY-MM-DD
    if (
        typeof date === "string" &&
        date.includes("-")
    ) {

        const parts =
            date.split("-");

        if (parts.length === 3) {

            return (
                parts[2] +
                "-" +
                parts[1] +
                "-" +
                parts[0]
            );
        }
    }

    return date;
}


function convertDateToISO(date) {

    if (!date) {
        return null;
    }


    // Browser date input normally gives YYYY-MM-DD
    if (
        date.includes("-") &&
        date.split("-")[0].length === 4
    ) {
        return date;
    }


    // Convert DD/MM/YYYY
    if (date.includes("/")) {

        const parts =
            date.split("/");

        if (parts.length === 3) {

            return (
                parts[2] +
                "-" +
                parts[1].padStart(2, "0") +
                "-" +
                parts[0].padStart(2, "0")
            );
        }
    }


    return date;
}


// ============================================================
// CURRENCY
// ============================================================

function formatCurrency(amount) {

    if (
        amount === null ||
        amount === undefined ||
        isNaN(amount)
    ) {

        return "₹0";
    }


    return (
        "₹" +
        Number(amount).toLocaleString("en-IN")
    );
}


// ============================================================
// DASHBOARD ERROR
// ============================================================

function showDashboardError() {

    setText(
        "totalApplications",
        "-"
    );

    setText(
        "approvedApplications",
        "-"
    );

    setText(
        "rejectedApplications",
        "-"
    );

    setText(
        "totalApprovedGrant",
        "₹-"
    );

    setText(
        "grantAmount",
        "₹-"
    );

    setText(
        "expenditureAmount",
        "₹-"
    );

    setText(
        "remainingAmount",
        "₹-"
    );
}


// ============================================================
// QUICK ACTION MESSAGE
// ============================================================

function showMessage(message) {

    alert(message);
}


// ============================================================
// HELPER
// ============================================================

function setText(id, value) {

    const element =
        document.getElementById(id);

    if (element) {

        element.textContent =
            value ?? "-";
    }
}