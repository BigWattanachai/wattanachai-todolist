$(function () {

    var LIST_COUNTER = 0;
    var List = function ($lobiList, options) {
        this.$lobiList = $lobiList;
        this.$options = options;
        this.$globalOptions = $lobiList.$options;
        this.$items = {};

        this._init();
    };

    List.prototype = {
        $lobiList: null,
        $el: null,
        $elWrapper: null,
        $options: {},
        $items: {},
        $globalOptions: {},
        $ul: null,
        $header: null,
        $title: null,
        $form: null,
        $footer: null,
        $body: null,

        eventsSuppressed: false,

        _init: function () {
            var me = this;
            me.suppressEvents();
            if (!me.$options.id) {
                me.$options.id = 'lobilist-list-' + (LIST_COUNTER++);
            }
            var $wrapper = $('<div style="width:100% !important;" class="lobilist-wrapper"></div>');
            var $div = $('<div id="' + me.$options.id + '" class="lobilist"></div>').appendTo($wrapper);

            if (me.$options.defaultStyle) {
                $div.addClass(me.$options.defaultStyle);
            }
            me.$el = $div;
            me.$elWrapper = $wrapper;
            me.$header = me._createHeader();
            me.$title = me._createTitle();
            me.$body = me._createBody();
            me.$ul = me._createList();
            if (me.$options.items) {
                me._createItems(me.$options.items);
            }
            me.$form = me._createForm();
            me.$body.append(me.$ul, me.$form);
            if (me.$globalOptions.sortable) {
                me._enableSorting();
            }

            me.resumeEvents();
        },

        addItem: function (item, errorCallback) {
            var me = this;
            if (me._triggerEvent('beforeItemAdd', [me, item]) === false) {
                return me;
            }

            item = me._processItemData(item);
            if (me.$globalOptions.actions.insert) {
                $.ajax(me.$globalOptions.actions.insert, {
                    data: item,
                    method: 'POST'
                })
                    .done(function (res) {
                        if (res.success) {
                            item.id = res.id;
                            me._addItemToList(item);
                        } else {
                            if (errorCallback && typeof errorCallback === 'function') {
                                errorCallback(res)
                            }
                        }
                    });
            } else {
                item.id = me.$lobiList.getNextId();
                me._addItemToList(item);
            }
            return me;
        },

        updateItem: function (item, errorCallback) {
            var me = this;
            if (me._triggerEvent('beforeItemUpdate', [me, item]) === false) {
                return me
            }
            if (item.date)
                item.date = this._dateToEpoch(item.date);

            $.ajax('../api/v1/todos/' + item.id, {
                data: JSON.stringify(item),
                method: 'PATCH',
                contentType: 'application/json'
            }).done(function (res) {
                if (res.message === 'success') {
                    me._updateItemInList(res.data);
                } else {
                    if (errorCallback && typeof errorCallback === 'function') {
                        errorCallback(res)
                    }
                }
            });
            return me;
        },

        saveOrUpdateItem: function (item, errorCallback) {
            var me = this;
            if (item.id) {
                me.updateItem(item, errorCallback);
            } else {
                me.addItem(item, errorCallback);
            }
            return me;
        },

        updateOderItem: function (item, errorCallback) {
            var me = this;
            $.ajax('../api/v1/todos/order', {
                data: JSON.stringify({"todoOrder": item}),
                method: 'PUT',
                contentType: 'application/json'
            }).done(function (res) {
                if (res.message === 'success') {
                } else {
                    if (errorCallback && typeof errorCallback === 'function') {
                        errorCallback(res)
                    }
                }
            });
            return me;
        },

        startTitleEditing: function () {
            var me = this;
            var input = me._createInput();
            me.$title.attr('data-old-title', me.$title.html());
            input.val(me.$title.html());
            input.insertAfter(me.$title);
            me.$title.addClass('hide');
            me.$header.addClass('title-editing');
            input[0].focus();
            input[0].select();
            return me;
        },

        finishTitleEditing: function () {
            var me = this;
            var $input = me.$header.find('input');
            var oldTitle = me.$title.attr('data-old-title');
            me.$title.html($input.val()).removeClass('hide').removeAttr('data-old-title');
            $input.remove();
            me.$header.removeClass('title-editing');
            me._triggerEvent('titleChange', [me, oldTitle, $input.val()]);
            return me;
        },

        cancelTitleEditing: function () {
            var me = this;
            var $input = me.$header.find('input');
            if ($input.length === 0) {
                return me;
            }
            me.$title.html(me.$title.attr('data-old-title')).removeClass('hide');
            $input.remove();
            me.$header.removeClass('title-editing');
            return me;
        },

        remove: function () {
            var me = this;
            me.$lobiList.$lists.splice(me.$el.index(), 1);
            me.$elWrapper.remove();

            return me;
        },

        editItem: function (id) {
            var me = this;
            var $item = me.$lobiList.$el.find('li[data-id=' + id + ']');
            var $form = $item.closest('.lobilist').find('.lobilist-add-todo-form');
            var $footer = $item.closest('.lobilist').find('.lobilist-footer');

            $form.removeClass('hide');
            $footer.addClass('hide');
            $form[0].id.value = $item.attr('data-id');
            $form[0].title.value = $item.find('.lobilist-item-title').html();
            $form[0].dueDate.value = $item.find('.lobilist-item-duedate').html() || '';
            return me;
        },
        suppressEvents: function () {
            this.eventsSuppressed = true;
            return this;
        },

        resumeEvents: function () {
            this.eventsSuppressed = false;
            return this;
        },

        _processItemData: function (item) {
            var me = this;
            return $.extend({}, me.$globalOptions.itemOptions, item);
        },

        _createHeader: function () {
            var me = this;
            var $header = $('<div>', {
                'class': 'lobilist-header'
            });
            var $actions = $('<div>', {
                'class': 'lobilist-actions'
            }).appendTo($header);
            if (me.$options.controls && me.$options.controls.length > 0) {
                if (me.$options.controls.indexOf('styleChange') > -1) {
                    $actions.append(me._createDropdownForStyleChange());
                }

                if (me.$options.controls.indexOf('edit') > -1) {
                    $actions.append(me._createEditTitleButton());
                    $actions.append(me._createFinishTitleEditing());
                    $actions.append(me._createCancelTitleEditing());
                }
                if (me.$options.controls.indexOf('add') > -1) {
                    $actions.append(me._createAddNewButton());
                }
                if (me.$options.controls.indexOf('remove') > -1) {
                    $actions.append(me._createCloseButton());
                }
            }
            me.$el.append($header);
            return $header;
        },

        _createTitle: function () {
            var me = this;
            var $title = $('<div>', {
                'class': 'lobilist-title',
                html: me.$options.title
            }).appendTo(me.$header);
            if (me.$options.controls && me.$options.controls.indexOf('edit') > -1) {
                $title.on('dblclick', function () {
                    me.startTitleEditing();
                });
            }
            return $title;
        },

        _createBody: function () {
            var me = this;
            return $('<div>', {
                'class': 'lobilist-body'
            }).appendTo(me.$el);

        },

        _createForm: function () {
            var me = this;
            var $form = $('<form>', {
                'class': 'lobilist-add-todo-form hide'
            });
            $('<input type="hidden" name="id">').appendTo($form);
            $('<div class="form-group">').append(
                $('<input>', {
                    'type': 'text',
                    name: 'title',
                    'class': 'form-control',
                    placeholder: 'TODO title'
                })
            ).appendTo($form);
            $('<div class="form-group">').append(
                $('<input>', {
                    'type': 'text',
                    name: 'dueDate',
                    'class': 'form-control',
                    placeholder: 'Date'
                })
            ).appendTo($form);
            var $ft = $('<div class="lobilist-form-footer">');
            $('<button>', {
                'class': 'btn btn-primary btn-sm btn-add-todo',
                html: 'Edit'
            }).appendTo($ft);
            $('<button>', {
                type: 'button',
                'class': 'btn btn-default btn-sm btn-discard-todo',
                html: '<i class="glyphicon glyphicon-remove-circle"></i>'
            }).click(function () {
                $form.addClass('hide');
            }).appendTo($ft);
            $ft.appendTo($form);

            me._formHandler($form);

            me.$el.append($form);
            return $form;
        },

        _formHandler: function ($form) {
            var me = this;
            $form.on('submit', function (ev) {
                ev.preventDefault();
                me._submitForm();
            });
        },

        _submitForm: function () {
            var me = this;
            if (!me.$form[0].title.value) {
                me._showFormError('title', 'Task cannot be empty');
                return;
            }
            if (!this._validateDateFormat(me.$form[0].dueDate.value).isValid()) {
                me._showFormError('dueDate', 'Invalid date format');
                return;
            }
            me.saveOrUpdateItem({
                id: me.$form[0].id.value,
                task: me.$form[0].title.value,
                date: me.$form[0].dueDate.value
            });
            me.$form.addClass('hide');
        },

        _createFooter: function () {
            var me = this;
            var $footer = $('<div>', {
                'class': 'lobilist-footer'
            });
            $('<button>', {
                type: 'button',
                'class': 'btn-link btn-show-form',
                'html': 'Add new'
            }).click(function () {
                me._resetForm();
                me.$form.removeClass('hide');
                $footer.addClass('hide');
            }).appendTo($footer);
            me.$el.append($footer);
            return $footer;
        },

        _createList: function () {
            var me = this;
            var $list = $('<ul>', {
                'class': 'lobilist-items'
            });
            me.$el.append($list);
            return $list;
        },

        _createItems: function (items) {
            var me = this;
            for (var i = 0; i < items.length; i++) {
                me._addItem(items[i]);
            }
        },

        _addItem: function (item) {
            var me = this;
            if (!item.todoId) {
                item.todoId = me.$lobiList.getNextId();
            }
            if (me._triggerEvent('beforeItemAdd', [me, item]) !== false) {
                item = me._processItemData(item);
                me._addItemToList(item);
            }
        },

        _createCheckbox: function () {
            var me = this;

            var $item = $('<input>', {
                'type': 'checkbox'
            });

            $item.change(function () {
                me._onCheckboxChange(this);
            });

            return $('<label>', {
                'class': 'checkbox-inline lobilist-check'
            }).append($item);
        },

        _onCheckboxChange: function (checkbox) {
            var me = this;
            var $this = $(checkbox);
            if ($this.prop('checked')) {
                me.saveOrUpdateItem({
                    'id': $this.closest('.lobilist-item').attr('data-id'),
                    'completed': true
                });
                me._triggerEvent('afterMarkAsDone', [me, $this])
            } else {
                me.saveOrUpdateItem({
                    'id': $this.closest('.lobilist-item').attr('data-id'),
                    'completed': false
                });
                me._triggerEvent('afterMarkAsUndone', [me, $this])
            }
            $this.closest('.lobilist-item').toggleClass('item-done');
        },

        _onImportantItemClick: function (id) {
            var me = this;
            me.saveOrUpdateItem({
                'id': id,
                'important': !me.$items[id].important
            });
        },

        _createDropdownForStyleChange: function () {
            var me = this;
            var $dropdown = $('<div>', {
                'class': 'dropdown'
            }).append(
                $('<button>', {
                    'type': 'button',
                    'data-toggle': 'dropdown',
                    'class': 'btn btn-default btn-xs',
                    'html': '<i class="glyphicon glyphicon-th"></i>'
                })
            );
            var $menu = $('<div>', {
                'class': 'dropdown-menu dropdown-menu-right'
            }).appendTo($dropdown);

            for (var i = 0; i < me.$globalOptions.listStyles.length; i++) {
                var st = me.$globalOptions.listStyles[i];
                $('<div class="' + st + '"></div>')
                    .on('mousedown', function (ev) {
                        ev.stopPropagation()
                    })
                    .click(function () {
                        var classes = me.$el[0].className.split(' ');
                        var oldClass = null;
                        for (var i = 0; i < classes.length; i++) {
                            if (me.$globalOptions.listStyles.indexOf(classes[i]) > -1) {
                                oldClass = classes[i];
                            }
                        }
                        me.$el.removeClass(me.$globalOptions.listStyles.join(" "))
                            .addClass(this.className);

                        me._triggerEvent('styleChange', [me, oldClass, this.className]);

                    })
                    .appendTo($menu);
            }
            return $dropdown;
        },

        _createEditTitleButton: function () {
            var me = this;
            var $btn = $('<button>', {
                'class': 'btn btn-default btn-xs',
                html: '<i class="glyphicon glyphicon-edit"></i>'
            });
            $btn.click(function () {
                me.startTitleEditing();
            });

            return $btn;
        },

        _createAddNewButton: function () {
            var me = this;
            var $btn = $('<button>', {
                'class': 'btn btn-default btn-xs',
                html: '<i class="glyphicon glyphicon-plus"></i>'
            });
            $btn.click(function () {
                var list = me.$lobiList.addList();
                list.startTitleEditing();
            });
            return $btn;
        },

        _createCloseButton: function () {
            var me = this;
            var $btn = $('<button>', {
                'class': 'btn btn-default btn-xs',
                html: '<i class="glyphicon glyphicon-remove"></i>'
            });
            $btn.click(me._onRemoveListClick);
            return $btn;
        },

        _onRemoveListClick: function () {
            var me = this;
            me._triggerEvent('beforeListRemove', [me]);
            me.remove();
            me._triggerEvent('afterListRemove', [me]);
            return me;
        },

        _createFinishTitleEditing: function () {
            var me = this;
            var $btn = $('<button>', {
                'class': 'btn btn-default btn-xs btn-finish-title-editing',
                html: '<i class="glyphicon glyphicon-ok-circle"></i>'
            });
            $btn.click(function () {
                me.finishTitleEditing();
            });
            return $btn;
        },

        _createCancelTitleEditing: function () {
            var me = this;
            var $btn = $('<button>', {
                'class': 'btn btn-default btn-xs btn-cancel-title-editing',
                html: '<i class="glyphicon glyphicon-remove-circle"></i>'
            });
            $btn.click(function () {
                me.cancelTitleEditing();
            });
            return $btn;
        },

        _createInput: function () {
            var me = this;
            var input = $('<input type="text" class="form-control">');
            input.on('keyup', function (ev) {
                if (ev.which === 13) {
                    me.finishTitleEditing();
                }
            });
            return input;
        },

        _showFormError: function (field, error) {
            var $fGroup = this.$form.find('[name="' + field + '"]').closest('.form-group')
                .addClass('has-error');
            $fGroup.find('.help-block').remove();
            $fGroup.append(
                $('<span class="help-block">' + error + '</span>')
            );
        },

        _resetForm: function () {
            var me = this;
            me.$form[0].reset();
            me.$form[0].id.value = "";
            me.$form.find('.form-group').removeClass('has-error').find('.help-block').remove();
        },

        _enableSorting: function () {
            var me = this;
            me.$el.find('.lobilist-items').sortable({
                connectWith: '.lobilist .lobilist-items',
                items: '.lobilist-item',
                handle: '.drag-handler',
                cursor: 'move',
                placeholder: 'lobilist-item-placeholder',
                forcePlaceholderSize: true,
                opacity: 0.9,
                revert: 70,
                start: function (event, ui) {
                    var todoItem = me.$items[ui.item.attr('data-id')];
                    ui.item.data("important", todoItem.important);
                    ui.item.data("todoId", ui.item.attr('data-id'));
                    ui.item.data("oldIndex", ui.item.index());
                },
                update: function (event, ui) {
                    $(this).sortable('refresh');
                    if (ui.item.data("important") === true) {
                        $(this).sortable('cancel');
                    } else {
                        var todoList = me._mapObjectToList(me.$items);
                        var importantCount = me._countImportantItem(todoList);
                        if (ui.item.index() < importantCount.length) {
                            $(this).sortable('cancel');
                        }
                    }
                },
                stop: function (event, ui) {
                    var itemOrder = $(this).sortable("toArray", {attribute: 'data-id'});
                    if (ui.item.data("oldIndex") !== ui.item.index())
                        me.updateOderItem(itemOrder);
                }
            });
        },

        _countImportantItem: function (todoList) {
            var anyUnImportant = true;
            return todoList.filter(function (todo) {
                if (todo.important === false) {
                    anyUnImportant = false;
                }
                return anyUnImportant === true && todo.important === true;
            });
        },

        _mapObjectToList: function (objectData) {
            return $.map(objectData, function (value, index) {
                return [value];
            });
        },

        _epochToDate: function (epoch) {
            return moment.utc(epoch).format('M/D/YY HH:mm')

        },

        _dateToEpoch: function (date) {
            return moment.utc(date, "M/D/YY HH:mm'").valueOf()

        },

        _validateDateFormat: function (date) {
            return moment.utc(date, 'M/D/YY HH:mm')
        },
        _addItemToList: function (item) {
            var me = this;
            var $li = $('<li>', {
                'data-id': item.todoId,
                'class': 'lobilist-item'
            });
            $li.append($('<div>', {
                'class': 'lobilist-item-title',
                'html': item.task
            }));

            if (item.date) {
                $li.append($('<div>', {
                    'class': 'lobilist-item-duedate',
                    html: this._epochToDate(item.date)
                }));
            }
            $li = me._addItemControls($li, item);
            if (item.completed) {
                $li.find('input[type=checkbox]').prop('checked', true);
                $li.addClass('item-done');
            }
            $li.data('lobiListItem', item);
            me.$ul.append($li);
            me.$items[item.todoId] = item;
            me._triggerEvent('afterItemAdd', [me, item]);

            return $li;
        },

        _addItemControls: function ($li, item) {
            var me = this;
            if (me.$options.useCheckboxes) {
                $li.append(me._createCheckbox());
            }
            var $itemControlsDiv = $('<div>', {
                'class': 'todo-actions'
            }).appendTo($li);

            if (me.$options.enableTodoEdit) {
                $itemControlsDiv.append($('<div>', {
                    'class': 'edit-todo todo-action',
                    html: '<i class="glyphicon glyphicon-pencil"></i>'
                }).click(function () {
                    me.editItem($(this).closest('li').data('id'));
                }));
            }
            if (me.$options.enableImportant) {
                var importantHtml = '<i class="star glyphicon glyphicon-star-empty"></i>';
                if (item.important) {
                    importantHtml = '<i class="star glyphicon glyphicon-star"></i>';
                }
                $itemControlsDiv.append($('<div>', {
                    'class': 'important-todo todo-action',
                    html: importantHtml
                }).click(function () {
                    $("i", this).toggleClass("glyphicon-star glyphicon-star-empty");
                    me._onImportantItemClick($(this).closest('li').data('id'));
                }));
            }

            $li.append($('<div>', {
                'class': 'drag-handler'
            }));
            return $li;
        },

        _onDeleteItemClick: function (item) {
            this.deleteItem(item);
        },

        _updateItemInList: function (item) {
            var me = this;
            var $li = me.$lobiList.$el.find('li[data-id="' + item.todoId + '"]');
            $li.find('input[type=checkbox]').prop('checked', item.completed);
            $li.find('.lobilist-item-title').html(item.task);
            $li.find('.lobilist-item-duedate').remove();
            if (item.date) {
                $li.append('<div class="lobilist-item-duedate">' + this._epochToDate(item.date) + '</div>');
            }
            $li.data('lobiListItem', item);
            $.extend(me.$items[item.todoId], item);
            me._triggerEvent('afterItemUpdate', [me, item]);
        },

        _triggerEvent: function (type, data) {
            var me = this;
            if (me.eventsSuppressed) {
                return;
            }
            if (me.$options[type] && typeof me.$options[type] === 'function') {
                return me.$options[type].apply(me, data);
            } else {
                return me.$el.trigger(type, data);
            }
        }
    };
//||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||

    var LobiList = function ($el, options) {
        this.$el = $el;
        this.init(options);
    };

    LobiList.prototype = {
        $el: null,
        $lists: [],
        $options: {},
        _nextId: 1,

        eventsSuppressed: false,

        init: function (options) {
            var me = this;
            me.suppressEvents();

            me.$options = this._processInput(options);
            me.$el.addClass('lobilists');
            if (me.$options.onSingleLine) {
                me.$el.addClass('single-line');
            }

            me._createLists();
            me._handleSortable();
            me._triggerEvent('init', [me]);
            me.resumeEvents();
        },

        _processInput: function (options) {
            options = $.extend({}, $.fn.lobiList.DEFAULT_OPTIONS, options);
            if (options.actions.load) {
                $.ajax(options.actions.load, {
                    async: false
                }).done(function (res) {
                    options.lists = res.lists;
                });
            }
            return options;
        },

        _createLists: function () {
            var me = this;
            for (var i = 0; i < me.$options.lists.length; i++) {
                me.addList(me.$options.lists[i]);
            }
            return me;
        },

        _handleSortable: function () {
            var me = this;
            if (me.$options.sortable) {
                me.$el.sortable({
                    items: '.lobilist-wrapper',
                    handle: '.lobilist-header',
                    cursor: 'move',
                    placeholder: 'lobilist-placeholder',
                    forcePlaceholderSize: true,
                    opacity: 0.9,
                    revert: 70,
                    update: function (event, ui) {
                        me._triggerEvent('afterListReorder', [me, ui.item.find('.lobilist').data('lobiList')]);
                    }
                });
            } else {
                me.$el.addClass('no-sortable');
            }
            return me;
        },

        addList: function (list) {
            var me = this;
            if (!(list instanceof List)) {
                list = new List(me, me._processListOptions(list));
            }
            if (me._triggerEvent('beforeListAdd', [me, list]) !== false) {
                me.$lists.push(list);
                me.$el.append(list.$elWrapper);
                list.$el.data('lobiList', list);
                me._triggerEvent('afterListAdd', [me, list]);
            }
            return list;
        },

        destroy: function () {
            var me = this;
            if (me._triggerEvent('beforeDestroy', [me]) !== false) {
                for (var i = 0; i < me.$lists.length; i++) {
                    me.$lists[i].remove();
                }
                if (me.$options.sortable) {
                    me.$el.sortable("destroy");
                }
                me.$el.removeClass('lobilists');
                if (me.$options.onSingleLine) {
                    me.$el.removeClass('single-line');
                }
                me.$el.removeData('lobiList');
                me._triggerEvent('afterDestroy', [me]);
            }

            return me;
        },

        getNextId: function () {
            return this._nextId++;
        },
        _processListOptions: function (listOptions) {
            var me = this;
            listOptions = $.extend({}, me.$options.listsOptions, listOptions);

            for (var i in me.$options) {
                if (me.$options.hasOwnProperty(i) && listOptions[i] === undefined) {
                    listOptions[i] = me.$options[i];
                }
            }
            return listOptions;
        },

        suppressEvents: function () {
            this.eventsSuppressed = true;
            return this;
        },

        resumeEvents: function () {
            this.eventsSuppressed = false;
            return this;
        },

        _triggerEvent: function (type, data) {
            var me = this;
            if (me.eventsSuppressed) {
                return;
            }
            if (me.$options[type] && typeof me.$options[type] === 'function') {
                return me.$options[type].apply(me, data);
            } else {
                return me.$el.trigger(type, data);
            }
        }
    };

    $.fn.lobiList = function (option) {
        var args = arguments;
        var ret;
        return this.each(function () {
            var $this = $(this);
            var data = $this.data('lobiList');
            var options = typeof option === 'object' && option;

            if (!data) {
                $this.data('lobiList', (data = new LobiList($this, options)));
            }
            if (typeof option === 'string') {
                args = Array.prototype.slice.call(args, 1);
                ret = data[option].apply(data, args);
            }
        });
    };
    $.fn.lobiList.DEFAULT_OPTIONS = {
        // Available style for lists
        'listStyles': ['lobilist-default', 'lobilist-danger', 'lobilist-success', 'lobilist-warning', 'lobilist-info', 'lobilist-primary'],
        // Default options for all lists
        listsOptions: {
            id: false,
            title: '',
            items: []
        },
        // Default options for all todo items
        itemOptions: {
            todoId: false,
            task: '',
            important: false,
            date: '',
            completed: false
        },

        lists: [],
        // Urls to communicate to backend for todos
        actions: {
            'load': '',
            'update': '',
            'insert': '',
            'delete': ''
        },
        // Whether to show checkboxes or not
        useCheckboxes: true,
        // Show/hide todo remove button
        enableImportant: true,
        // Show/hide todo edit button
        enableTodoEdit: true,
        // Whether to make lists and todos sortable
        sortable: true,
        // Default action buttons for all lists
        controls: ['edit', 'add', 'remove', 'styleChange'],
        //List style
        defaultStyle: 'lobilist-default',
        // Whether to show lists on single line or not
        onSingleLine: true,

        init: null,
        beforeDestroy: null,
        afterDestroy: null,
        beforeListAdd: null,
        afterListAdd: null,
        beforeListRemove: null,
        afterListRemove: null,
        beforeItemAdd: null,
        afterItemAdd: null,
        beforeItemUpdate: null,
        afterItemUpdate: null,
        beforeItemDelete: null,
        afterItemDelete: null,
        afterListReorder: null,
        afterItemReorder: null,
        afterMarkAsDone: null,
        afterMarkAsUndone: null,
        afterStarMarkAsDone: null,
        afterStarMarkAsUndone: null,
        beforeAjaxSent: null,
        styleChange: null,
        titleChange: null
    };
});