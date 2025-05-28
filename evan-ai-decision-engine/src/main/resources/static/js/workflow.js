// CodeMirror editors
let configEditor, scriptEditor;
let graph, paper;
let currentWorkflow = null;
let nodes = [];

function initWorkflowDiagram() {
    // Ensure the container exists before initialization
    const container = document.getElementById('workflowDiagram');
    if (!container) {
        console.error('Workflow diagram container not found');
        return;
    }

    graph = new joint.dia.Graph();
    paper = new joint.dia.Paper({
        el: container,
        model: graph,
        width: container.offsetWidth || 800,
        height: 500,
        gridSize: 10,
        drawGrid: true,
        background: {
            color: '#f8f9fa'
        },
        interactive: true
    });

    // Handle node selection with safer property access
    paper.on('cell:pointerclick', function(cellView) {
        if (cellView && cellView.model && cellView.model.id) {
            const nodeId = cellView.model.id;
            const node = nodes.find(n => n && n.diagramId === nodeId);
            if (node) {
                editNode(node);
            }
        }
    });
}

// Load all workflows
function loadWorkflows() {
    $.get('/api/workflows', function(data) {
        const tbody = $('#workflowsTableBody');
        tbody.empty();

        data.forEach(workflow => {
            const row = `
                <tr>
                    <td>${workflow.name}</td>
                    <td>${workflow.description || ''}</td>
                    <td>${workflow.enabled ? 'Enabled' : 'Disabled'}</td>
                    <td>
                        <button class="btn btn-sm btn-info edit-workflow-btn" data-id="${workflow.id}">Edit</button>
                        <button class="btn btn-sm btn-danger delete-workflow-btn" data-id="${workflow.id}">Delete</button>
                    </td>
                </tr>
            `;
            tbody.append(row);
        });

        $('#workflowsList').show();
        $('#workflowDesigner').hide();
    });
}

// Load workflow for editing
function loadWorkflow(id) {
    $.get(`/api/workflows/${id}`, function(workflow) {
        currentWorkflow = workflow;
        $('#workflowId').val(workflow.id);
        $('#workflowName').val(workflow.name);
        $('#workflowDescription').val(workflow.description || '');
        $('#workflowEnabled').prop('checked', workflow.enabled);

        $('#workflowsList').hide();
        $('#workflowDesigner').show();

        // Load nodes for this workflow
        loadWorkflowNodes(workflow.id);
    });
}

// Load nodes for a workflow
function loadWorkflowNodes(workflowId) {
    $.get(`/api/workflows/${workflowId}/nodes`, function(data) {
        nodes = data;
        renderWorkflowDiagram();
    });
}

// Render the workflow diagram
function renderWorkflowDiagram() {
    graph.clear();

    // Add nodes to diagram
    let lastX = 50;
    const y = 150;

    nodes.sort((a, b) => a.position - b.position).forEach((node, index) => {
        // Create JointJS node
        const rect = new joint.shapes.standard.Rectangle({
            id: node.id.toString(),
            position: { x: lastX, y: y },
            size: { width: 160, height: 80 },
            attrs: {
                body: {
                    fill: '#d1e7dd',
                    stroke: '#198754',
                    strokeWidth: 2,
                    rx: 5,
                    ry: 5
                },
                label: {
                    text: `${node.position}. ${node.name}`,
                    fill: '#000',
                    fontSize: 14,
                    fontWeight: 'bold',
                    textWrap: {
                        width: 150,
                        height: 60
                    }
                }
            }
        });

        graph.addCell(rect);
        node.diagramId = rect.id;

        // Create link to next node if not the last one
        if (index < nodes.length - 1) {
            const link = new joint.shapes.standard.Link({
                source: { id: rect.id },
                target: { x: lastX + 250, y: y + 40 },
                attrs: {
                    line: {
                        stroke: '#198754',
                        strokeWidth: 2,
                        targetMarker: {
                            type: 'path',
                            d: 'M 10 -5 0 0 10 5 z'
                        }
                    }
                },
                labels: [
                    {
                        position: 0.5,
                        attrs: {
                            text: {
                                text: '→',
                                fontSize: 16
                            }
                        }
                    }
                ]
            });
            graph.addCell(link);
        }

        lastX += 250;
    });

    if (nodes.length > 0) {
        paper.fitToContent({ padding: 50 });
    }
}

// Add new node
function addNode() {
    if (!currentWorkflow) {
        alert('Please create or select a workflow first');
        return;
    }

    const newPosition = nodes.length > 0 ? Math.max(...nodes.map(n => n.position)) + 1 : 1;

    $('#nodeId').val('');
    $('#nodeName').val('New Node');
    $('#nodePosition').val(newPosition);
    $('#nodeToolType').val('API');

    if (configEditor) configEditor.setValue('{\n  "endpoint": "https://api.example.com",\n  "method": "GET"\n}');
    else $('#nodeToolConfig').val('{\n  "endpoint": "https://api.example.com",\n  "method": "GET"\n}');

    if (scriptEditor) scriptEditor.setValue('');
    else $('#nodeToolScript').val('');

    $('#nodeEditor').show();
}

// Edit existing node
function editNode(node) {
    $('#nodeId').val(node.id);
    $('#nodeName').val(node.name);
    $('#nodePosition').val(node.position);
    $('#nodeToolType').val(node.toolType);

    initCodeMirrorEditors();

    if (configEditor) configEditor.setValue(node.toolConfig || '');
    else $('#nodeToolConfig').val(node.toolConfig || '');

    if (scriptEditor) scriptEditor.setValue(node.toolScript || '');
    else $('#nodeToolScript').val(node.toolScript || '');

    $('#nodeEditor').show();
}

// Initialize CodeMirror editors
function initCodeMirrorEditors() {
    if (!configEditor) {
        configEditor = CodeMirror.fromTextArea(document.getElementById('nodeToolConfig'), {
            mode: { name: 'javascript', json: true },
            lineNumbers: true,
            lineWrapping: true,
            theme: 'default'
        });
        configEditor.on('change', function(cm) {
            cm.save();
            autoResize(cm);
        });
    }

    if (!scriptEditor) {
        scriptEditor = CodeMirror.fromTextArea(document.getElementById('nodeToolScript'), {
            mode: 'groovy',
            lineNumbers: true,
            lineWrapping: true,
            theme: 'default'
        });
        scriptEditor.on('change', function(cm) {
            cm.save();
            autoResize(cm);
        });
    }

    setTimeout(() => {
        if (configEditor) {
            configEditor.refresh();
            autoResize(configEditor);
        }
        if (scriptEditor) {
            scriptEditor.refresh();
            autoResize(scriptEditor);
        }
    }, 100);
}

// Auto-resize CodeMirror to fit content
function autoResize(cm) {
    let scroller = cm.getScrollerElement();
    scroller.style.height = 'auto';
    let height = Math.min(200, Math.max(80, cm.getScrollInfo().height + 8));
    scroller.style.height = height + 'px';
    cm.refresh();
}

// Save node (create or update)
function saveNode(e) {
    e.preventDefault();

    if (configEditor) configEditor.save();
    if (scriptEditor) scriptEditor.save();

    // Ensure we have a current workflow
    if (!currentWorkflow || !currentWorkflow.id) {
        alert('Please save the workflow first before adding nodes');
        return;
    }

    // Validate required fields
    const nodeName = $('#nodeName').val();
    const toolType = $('#nodeToolType').val();
    let toolConfig = $('#nodeToolConfig').val();

    if (!nodeName || !toolType) {
        alert('Name and Tool Type are required');
        return;
    }

    // Ensure tool config is at least an empty object
    if (!toolConfig || toolConfig.trim() === '') {
        toolConfig = '{}';
    }

    const nodeData = {
        id: $('#nodeId').val() || null,
        name: nodeName,
        position: parseInt($('#nodePosition').val()),
        toolType: toolType,
        toolConfig: toolConfig,
        toolScript: $('#nodeToolScript').val() || ''
    };

    if (nodeData.id) {
        // Update existing node
        $.ajax({
            url: `/api/workflows/nodes/${nodeData.id}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(nodeData),
            success: function(updatedNode) {
                const index = nodes.findIndex(n => n.id == updatedNode.id);
                if (index >= 0) {
                    nodes[index] = updatedNode;
                }
                renderWorkflowDiagram();
                $('#nodeEditor').hide();
            },
            error: function(xhr) {
                alert('Save failed: ' + xhr.responseText);
            }
        });
    } else {
        // Create new node
        console.log('Creating new node for workflow ID:', currentWorkflow.id);
        $.ajax({
            url: `/api/workflows/${currentWorkflow.id}/nodes`,
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(nodeData),
            success: function(newNode) {
                nodes.push(newNode);
                renderWorkflowDiagram();
                $('#nodeEditor').hide();
            },
            error: function(xhr) {
                console.error('Error response:', xhr.responseText);
                alert('Save failed: ' + xhr.responseText);
            }
        });
    }
}

// Delete node
function deleteNode() {
    const nodeId = $('#nodeId').val();
    if (!nodeId) return;

    if (confirm('Are you sure you want to delete this node?')) {
        $.ajax({
            url: `/api/workflows/nodes/${nodeId}`,
            method: 'DELETE',
            success: function() {
                nodes = nodes.filter(n => n.id != nodeId);
                renderWorkflowDiagram();
                $('#nodeEditor').hide();
            }
        });
    }
}

// Save workflow
function saveWorkflow() {
    const workflowData = {
        id: $('#workflowId').val() || null,
        name: $('#workflowName').val(),
        description: $('#workflowDescription').val(),
        enabled: $('#workflowEnabled').is(':checked')
    };

    if (workflowData.id) {
        // Update existing workflow
        $.ajax({
            url: `/api/workflows/${workflowData.id}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(workflowData),
            success: function(updatedWorkflow) {
                currentWorkflow = updatedWorkflow;
                alert('Workflow updated successfully');
            },
            error: function(xhr) {
                alert('Save failed: ' + xhr.responseText);
            }
        });
    } else {
        // Create new workflow
        $.ajax({
            url: '/api/workflows',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(workflowData),
            success: function(newWorkflow) {
                currentWorkflow = newWorkflow;
                $('#workflowId').val(newWorkflow.id);
                alert('Workflow created successfully');
            },
            error: function(xhr) {
                alert('Save failed: ' + xhr.responseText);
            }
        });
    }
}

// Test workflow
function testWorkflow() {
    if (!currentWorkflow || !currentWorkflow.id) {
        alert('Please save the workflow first');
        return;
    }

    $('#testResultContainer').hide();
    $('#testInput').val('');
    $('#testWorkflowModal').modal('show');
}

// Run workflow test
function runTest() {
    const testInput = $('#testInput').val();

    $.ajax({
        url: `/api/workflows/${currentWorkflow.id}/execute`,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ request: testInput }),
        success: function(result) {
            $('#testResult').text(typeof result === 'object' ? JSON.stringify(result, null, 2) : result);
            $('#testResultContainer').show();
        },
        error: function(xhr) {
            $('#testResult').text('Error: ' + xhr.responseText);
            $('#testResultContainer').show();
        }
    });
}

// Format JSON
function formatNodeConfig() {
    let editor = configEditor;
    let val = editor ? editor.getValue() : $('#nodeToolConfig').val();
    try {
        let formatted = JSON.stringify(JSON.parse(val), null, 2);
        if (editor) {
            editor.setValue(formatted);
            editor.refresh();
            autoResize(editor);
        } else {
            $('#nodeToolConfig').val(formatted);
        }
    } catch (e) {
        alert('Invalid JSON: ' + e.message);
    }
}

// Initialize page
$(function() {
       // Check if JointJS is properly loaded before initialization
       if (typeof joint === 'undefined') {
           console.error('JointJS library not loaded');
           return;
       }

       // Initialize JointJS diagram
       initWorkflowDiagram();

    // Button event handlers
$('#createWorkflowBtn').click(function() {
    currentWorkflow = null;
    $('#workflowId').val('');
    $('#workflowName').val('New Workflow');
    $('#workflowDescription').val('');
    $('#workflowEnabled').prop('checked', true);
    nodes = [];
    graph.clear();
    $('#workflowsList').hide();
    $('#workflowDesigner').show();
    $('#nodeEditor').hide();

    // Prompt user to save the workflow first
    alert('Please save the workflow before adding nodes');
});

    $('#listWorkflowsBtn').click(loadWorkflows);
    $('#addNodeBtn').click(addNode);
    $('#saveWorkflowBtn').click(saveWorkflow);
    $('#testWorkflowBtn').click(testWorkflow);
    $('#formatNodeConfigBtn').click(formatNodeConfig);
    $('#nodeForm').submit(saveNode);
    $('#deleteNodeBtn').click(deleteNode);
    $('#cancelNodeBtn').click(function() {
        $('#nodeEditor').hide();
    });
    $('#runTestBtn').click(runTest);

    // Event delegation for workflow list actions
    $(document).on('click', '.edit-workflow-btn', function() {
        const id = $(this).data('id');
        loadWorkflow(id);
    });

    $(document).on('click', '.delete-workflow-btn', function() {
        const id = $(this).data('id');
        if (confirm('Are you sure you want to delete this workflow?')) {
            $.ajax({
                url: `/api/workflows/${id}`,
                method: 'DELETE',
                success: loadWorkflows
            });
        }
    });

    // Initial load
    loadWorkflows();
});