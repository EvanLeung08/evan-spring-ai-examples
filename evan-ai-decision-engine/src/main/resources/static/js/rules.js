// Helper to serialize form to JSON, handling checkbox
function getRuleFormData() {
    return {
        id: $("#ruleId").val() || null,
        name: $("#ruleName").val(),
        condition: $("#ruleCondition").val(),
        expression: $("#ruleExpression").val(),
        toolType: $("#ruleToolType").val(),
        toolConfig: $("#ruleToolConfig").val(),
        priority: parseInt($("#rulePriority").val(), 10),
        enabled: $("#ruleEnabled").is(":checked")
    };
}

function reloadRulesTable() {
    $.get("/api/rules", function(data) {
        let tbody = $("#rulesTable tbody");
        tbody.empty();
        data.content.forEach(function(rule) {
            let row = `<tr>
                <td>${rule.name}</td>
                <td>${rule.condition}</td>
                <td>${rule.expression}</td>
                <td>${rule.toolType}</td>
                <td>${rule.priority}</td>
                <td>${rule.enabled ? 'Enabled' : 'Disabled'}</td>
                <td>
                    <button class="btn btn-sm btn-info" data-id="${rule.id}" onclick="editRule(this)">Edit</button>
                    <button class="btn btn-sm btn-danger" data-id="${rule.id}" onclick="deleteRule(this)">Delete</button>
                </td>
            </tr>`;
            tbody.append(row);
        });
    });
}

// Always clear form when opening for add
$('.btn-primary[data-bs-target="#ruleModal"]').on('click', function() {
    $("#ruleForm")[0].reset();
    $("#ruleId").val('');
});

// editRule only fills the form and shows the modal
function editRule(btn) {
    let id = $(btn).data("id");
    $("#ruleModal").modal("show"); // Show modal first
    $.get(`/api/rules/${id}`, function(rule) {
        $("#ruleId").val(rule.id);
        $("#ruleName").val(rule.name);
        $("#ruleCondition").val(rule.condition);
        $("#ruleExpression").val(rule.expression);
        $("#ruleToolType").val(rule.toolType);
        $("#ruleToolConfig").val(rule.toolConfig);
        $("#rulePriority").val(rule.priority);
        $("#ruleEnabled").prop("checked", rule.enabled);
    });
}

function deleteRule(btn) {
    let id = $(btn).data("id");
    if (confirm("Delete this rule?")) {
        $.ajax({
            url: `/api/rules/${id}`,
            method: "DELETE",
            success: reloadRulesTable
        });
    }
}

$(function() {
    reloadRulesTable();

    // Reset form when modal is shown for add
    $('#ruleModal').on('show.bs.modal', function (event) {
        let button = $(event.relatedTarget);
        if (!button || !button.hasClass('btn-info')) {
            // Clear form for add
            $("#ruleForm")[0].reset();
            $("#ruleId").val('');
        }
    });

    // Form submit handler
    $("#ruleForm").submit(function (e) {
        e.preventDefault();
        const data = getRuleFormData();
        let method = data.id ? "PUT" : "POST";
        let url = data.id ? `/api/rules/${data.id}` : "/api/rules";
        $.ajax({
            url: url,
            method: method,
            contentType: "application/json",
            data: JSON.stringify(data),
            success: function () {
                $("#ruleModal").modal("hide");
                reloadRulesTable();
                $("#ruleForm")[0].reset();
            },
            error: function(xhr) {
                alert("Save failed: " + xhr.responseText);
            }
        });
    });
});