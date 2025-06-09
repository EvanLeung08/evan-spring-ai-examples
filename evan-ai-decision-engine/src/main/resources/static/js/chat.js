function appendMessage(sender, text, isAI) {
    let bubbleClass = isAI ? "chat-bubble ai" : "chat-bubble";
    let senderClass = isAI ? "chat-ai" : "chat-user";
    // Ensure the text is properly escaped and newlines are preserved
    const safeText = text ? text.replace(/</g, '&lt;').replace(/>/g, '&gt;') : '';
    let html = `<div class="chat-message">
        <span class="${senderClass}">${sender}:</span>
        <span class="${bubbleClass}" id="msg-${Date.now()}">${safeText}</span>
    </div>`;
    $("#chatBox").append(html);
    $("#chatBox").scrollTop($("#chatBox")[0].scrollHeight);
}

    let lastAIMessageId = null;
    let lastAIText = "";

   $("#chatForm").submit(function(e) {
    e.preventDefault();
    let input = $("#userInput").val();
    if (!input.trim()) return;
    appendMessage("You", input, false);
    $("#userInput").val("");
    // Add AI typing indicator
    let typingId = "typing-" + Date.now();
    $("#chatBox").append(`<div class="chat-message typing" id="${typingId}">AI is typing...</div>`);
    $("#chatBox").scrollTop($("#chatBox")[0].scrollHeight);

    // Start SSE stream
    let aiMsgId = "ai-msg-" + Date.now();
    lastAIMessageId = aiMsgId;
    lastAIText = "";
    // Only append the AI message bubble ONCE
    $("#chatBox").append(`<div class="chat-message"><span class="chat-ai">AI:</span> <span class="chat-bubble ai" id="${aiMsgId}"></span></div>`);
    $("#chatBox").scrollTop($("#chatBox")[0].scrollHeight);

    let source = new EventSourcePolyfill("/api/rules/chat/sse?request=" + encodeURIComponent(input));
source.onmessage = function(event) {
    // The backend sends the full text so far, properly format it
    const formattedText = event.data
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/\n/g, '<br>');

    // Update the message with the properly formatted content
    $("#" + aiMsgId).html(formattedText);
    $("#chatBox").scrollTop($("#chatBox")[0].scrollHeight);
};
    source.onerror = function() {
        $("#" + typingId).remove();
        source.close();
    };
    source.onopen = function() {
        $("#" + typingId).remove();
    };
});

function EventSourcePolyfill(url) {
    let controller = new AbortController();
    let listeners = {};

    // The URL already contains the correct endpoint (/api/rules/chat/sse)
    // We need to keep it as /api/rules/chat to match the controller endpoint
    let apiUrl = url.replace("/chat/sse", "/chat");

    fetch(apiUrl, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Accept": "text/event-stream"  // Add proper accept header
        },
        body: JSON.stringify({request: decodeURIComponent(url.split("=")[1])}),
        signal: controller.signal
    }).then(resp => {
        // Check if we received a login page instead of the expected stream
        if (resp.headers.get('content-type').includes('text/html')) {
            if (listeners.onmessage) {
                listeners.onmessage({data: "Error: Authentication required. Please log in."});
            }
            if (listeners.onerror) listeners.onerror();
            return;
        }

        const reader = resp.body.getReader();
        let buffer = ""; // Buffer for incomplete lines
        function read() {
            reader.read().then(({done, value}) => {
                if (done) {
                    if (listeners.onerror) listeners.onerror();
                    return;
                }
                let chunk = new TextDecoder().decode(value);
                buffer += chunk;
                let lines = buffer.split("\n");
                buffer = lines.pop(); // Save incomplete line

                // Process each complete line
                lines.forEach(line => {
                    if (line.startsWith("data:")) {
                        const data = line.substring(5).trim();
                        if (listeners.onmessage) {
                            listeners.onmessage({data: data});
                        }
                    }
                });
                read();
            });
        }
        if (listeners.onopen) listeners.onopen();
        read();
    }).catch((error) => {
        console.error("EventSource error:", error);
        if (listeners.onmessage) {
            listeners.onmessage({data: "Error: Connection failed. Please try again."});
        }
        if (listeners.onerror) listeners.onerror();
    });

    this.onmessage = null;
    this.onerror = null;
    this.onopen = null;
    this.close = () => controller.abort();
    Object.defineProperty(this, "onmessage", {
        set: fn => listeners.onmessage = fn
    });
    Object.defineProperty(this, "onerror", {
        set: fn => listeners.onerror = fn
    });
    Object.defineProperty(this, "onopen", {
        set: fn => listeners.onopen = fn
    });
}