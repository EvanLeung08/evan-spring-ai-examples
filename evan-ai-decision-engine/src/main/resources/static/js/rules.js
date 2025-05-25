

// Helper to serialize form to JSON, handling checkbox and toolScript
function getRuleFormData() {
    return {
        id: $("#ruleId").val() || null,
        name: $("#ruleName").val(),
        condition: $("#ruleCondition").val(),
        expression: $("#ruleExpression").val(),
        toolType: $("#ruleToolType").val(),
        toolConfig: $("#ruleToolConfig").val(),
        priority: parseInt($("#rulePriority").val(), 10),
        toolScript: $("#ruleToolScript").val(),
        enabled: $("#ruleEnabled").is(":checked")
    };
}

function reloadRulesTable() {
    $.get("/api/rules", function(data) {
        let tbody = $("#rulesTable tbody");
        tbody.empty();
        data.content.forEach(function(rule) {
            let scriptBtn = rule.toolScript && rule.toolScript.trim().length > 0
                ? `<button class="btn btn-sm btn-secondary" onclick="viewScript('${rule.id}')">View Script</button>`
                : '';
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
                    ${scriptBtn}
                </td>
            </tr>`;
            tbody.append(row);
        });
    });
}

// CodeMirror editors
let toolConfigEditor, toolScriptEditor;

function initCodeMirrorEditors() {
    if (!toolConfigEditor) {
        toolConfigEditor = CodeMirror.fromTextArea(document.getElementById("ruleToolConfig"), {
            mode: {name: "javascript", json: true},
            lineNumbers: true,
            lineWrapping: true,
            theme: "default"
        });
        toolConfigEditor.on("change", function(cm) {
            cm.save();
            autoResize(cm);
        });
    }
    if (!toolScriptEditor) {
        toolScriptEditor = CodeMirror.fromTextArea(document.getElementById("ruleToolScript"), {
            mode: "groovy",
            lineNumbers: true,
            lineWrapping: true,
            theme: "default"
        });
        toolScriptEditor.on("change", function(cm) {
            cm.save();
            autoResize(cm);
        });
    }
}

// Auto-resize CodeMirror to fit content
function autoResize(cm) {
    let scroller = cm.getScrollerElement();
    scroller.style.height = "auto";
    let height = Math.min(300, Math.max(80, cm.getScrollInfo().height + 8));
    scroller.style.height = height + "px";
    cm.refresh();
}

// Always clear form when opening for add
$('.btn-primary[data-bs-target="#ruleModal"]').on('click', function() {
    $("#ruleForm")[0].reset();
    $("#ruleId").val('');
    if (toolConfigEditor) toolConfigEditor.setValue("");
    if (toolScriptEditor) toolScriptEditor.setValue("");
});

// editRule fills the form and shows the modal, including toolScript
function editRule(btn) {
    let id = $(btn).data("id");
    $("#ruleModal").modal("show");
    $.get(`/api/rules/${id}`, function(rule) {
        $("#ruleId").val(rule.id);
        $("#ruleName").val(rule.name);
        $("#ruleCondition").val(rule.condition);
        $("#ruleExpression").val(rule.expression);
        $("#ruleToolType").val(rule.toolType);
        $("#rulePriority").val(rule.priority);
        $("#ruleEnabled").prop("checked", rule.enabled);

        // Set CodeMirror content
        if (toolConfigEditor) toolConfigEditor.setValue(rule.toolConfig || "");
        else $("#ruleToolConfig").val(rule.toolConfig || "");

        if (toolScriptEditor) toolScriptEditor.setValue(rule.toolScript || "");
        else $("#ruleToolScript").val(rule.toolScript || "");
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

    // Initialize CodeMirror editors when modal is shown
    $('#ruleModal').on('shown.bs.modal', function () {
        initCodeMirrorEditors();
        setTimeout(function() {
            if (toolConfigEditor) {
                toolConfigEditor.refresh();
                autoResize(toolConfigEditor);
            }
            if (toolScriptEditor) {
                toolScriptEditor.refresh();
                autoResize(toolScriptEditor);
            }
        }, 100);
    });

    // Reset form when modal is shown for add
    $('#ruleModal').on('show.bs.modal', function (event) {
        let button = $(event.relatedTarget);
        if (!button || !button.hasClass('btn-info')) {
            $("#ruleForm")[0].reset();
            $("#ruleId").val('');
            if (toolConfigEditor) toolConfigEditor.setValue("");
            if (toolScriptEditor) toolScriptEditor.setValue("");
        }
    });

    // Form submit handler
    $("#ruleForm").submit(function (e) {
        if (toolConfigEditor) toolConfigEditor.save();
        if (toolScriptEditor) toolScriptEditor.save();
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
                if (toolConfigEditor) toolConfigEditor.setValue("");
                if (toolScriptEditor) toolScriptEditor.setValue("");
            },
            error: function(xhr) {
                alert("Save failed: " + xhr.responseText);
            }
        });
    });

    // Format JSON in toolConfig
    $("#formatToolConfigBtn").on("click", function() {
        let editor = toolConfigEditor;
        let val = editor ? editor.getValue() : $("#ruleToolConfig").val();
        try {
            let formatted = JSON.stringify(JSON.parse(val), null, 2);
            if (editor) {
                editor.setValue(formatted);
                editor.refresh();
                autoResize(editor);
            } else {
                $("#ruleToolConfig").val(formatted);
            }
        } catch (e) {
            alert("Invalid JSON: " + e.message);
        }
    });

    // Format Groovy in toolScript (using js-beautify's js_beautify as a best-effort)
    $("#formatToolScriptBtn").on("click", function() {
        let editor = toolScriptEditor;
        let val = editor ? editor.getValue() : $("#ruleToolScript").val();
        try {
            // js_beautify is not perfect for Groovy, but improves indentation
            let formatted = js_beautify(val, { indent_size: 2 });
            if (editor) {
                editor.setValue(formatted);
                editor.refresh();
                autoResize(editor);
            } else {
                $("#ruleToolScript").val(formatted);
            }
        } catch (e) {
            alert("Format error: " + e.message);
        }
    });
});

function viewScript(ruleId) {
    $.get(`/api/rules/${ruleId}`, function(rule) {
        $("#scriptContent").text(rule.toolScript || "(No script)");
        $("#scriptModal").modal("show");
    });
}


/*Summary:
No browser tool can guarantee Groovy syntax correctness.
For strict syntax, use a backend Java Groovy formatter or require users to use an IDE for formatting.
Do not use js-beautify for Groovy.*/
$(document).on("click", "#formatViewScriptBtn", function() {
    let content = $("#scriptContent").text();
    try {
        let formatted = js_beautify(content, { indent_size: 2 });
        $("#scriptContent").text(formatted);
    } catch (e) {
        alert("Format error: " + e.message);
    }
});