AJS.$(function () {
    
	AJS.$("#add-botsing-config-button").click(showAddDialog);
	AJS.$("#edit-botsing-server-config-button").click(showEditServerDialog);
	AJS.$(".edit-botsing-config-button").click(showEditDialog);
    AJS.$(".remove-botsing-config-button, .activity-botsing-config-button, .clear-botsing-config-button").click(function () {
        getActionTriggerElement(this).closest("tr").find("." + AJS.$(this).attr("data-form")).submit();
    });

    function serializeForm(form) {
        var disabled = form.find(':disabled').prop('disabled', false);
        var serializedData = form.serialize();
        disabled.prop('disabled', true);
        return serializedData;
    }

    function showEditServerDialog() {
        var interior = AJS.$("#edit-botsing-server-config-container").clone();

        showDialog(interior, "edit-botsing-server-config-dialog", AJS.I18n.getText("botsing.buttons.edit"));
    }
    
    function showAddDialog() {
        var interior = AJS.$("#add-botsing-config-container").clone();

        showDialog(interior, "add-botsing-config-dialog", AJS.I18n.getText("botsing.buttons.add"));
    }

    function getActionTriggerElement(action) {
        return AJS.$("[href='#" + AJS.$(action).closest("div").attr("id") + "']");
    }

    function showEditDialog() {
        var interior = AJS.$("#edit-botsing-config-container").clone();

        showDialog(interior, "edit-botsing-config-dialog", AJS.I18n.getText("botsing.buttons.edit"));
    }

    function showDialog(interior, dialogId, headerText) {
        var dialog = new AJS.Dialog({
            width: 600,
            height: 600,
            id: dialogId
        });

        JIRA.bind("Dialog.beforeHide", function (event, dialog, reason) {
            return dialog.attr("id") !== dialogId || reason !== "esc";
        });

        dialog.addHeader(headerText);

        dialog.addPanel("Config", interior, "");
        interior.show();
        var messageContainerId = "#" + dialogId + " .message-container";

        dialog.addButton(
            AJS.I18n.getText("botsing.buttons.ok"),
            function () {
                var form = interior.find("form");

                AJS.$.ajax({
                    url: form.attr("action"),
                    type: "POST",
                    dataType: "json",
                    data: serializeForm(form),
                    async: false,
                    error: function (xhr) {
                        message(messageContainerId,
                            xhr.statusText + (": " + xhr.responseText) || "",
                            AJS.messages.error);
                    },
                    success: function () {
                        dialog.remove();
                        form.get(0).reset();
                        location.reload();
                    }
                });

            },
            "aui-button"
        );

        dialog.addCancel(
            AJS.I18n.getText("botsing.buttons.cancel"),
            function (dialog) {
                dialog.remove();
            }
        );

        dialog.show();
    }

    function message(id, text, func) {
        AJS.$(id).empty();
        func.apply(AJS.messages, [id, {
            closeable: true,
            body: text
        }]);
    }
});