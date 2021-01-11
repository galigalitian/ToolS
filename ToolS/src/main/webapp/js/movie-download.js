$(function() {
	/*剧集相关*/
	$('.group-title').click(function() {
	    var groupTitle = $(this).attr('groupTitle');
	    var $downloadUl = $(this).parent().find('ul.' + groupTitle);
	    var $icon_caret = $(this).find('i.icon-caret')
	    if ($icon_caret.hasClass('icon-caret-up')) {
	        $icon_caret.removeClass('icon-caret-up').addClass('icon-caret-down');
	        $icon_caret.next().html('显示');
	        $downloadUl.hide();
	    } else if ($icon_caret.hasClass('icon-caret-down')) {
	        $icon_caret.removeClass('icon-caret-down').addClass('icon-caret-up');
	        $icon_caret.next().html('隐藏');
	        $downloadUl.show();
	        changeDownloadList($downloadUl);
	    }
	});

	/**
	 * 剧集全选
	 */
	$('.all-sel').click(function() {
	    var url_checkbox = $(this).parents('li').parent().find('.url-checkbox');
	    if ($(this).prop('checked'))
	        url_checkbox.prop('checked', true);
	    else url_checkbox.prop('checked', false);
	});
	/*剧集选择*/
	$('.url-checkbox').click(function() {
	    var parentUl = $(this).parents('li').parent();
	    var allf = true;
	    parentUl.find('.url-checkbox').each(function() {
	        if (!$(this).prop('checked')) {
	            allf = false;
	            return false;
	        }
	    });
	    if (allf) parentUl.find('.all-sel').prop('checked', true);
	    else parentUl.find('.all-sel').prop('checked', false);
	});
	/**
	 * 获取剧集下载链接
	 */
	$('.batch-download-a').click(function() {
	    var li_obj_arr = $(this).parents('li').parent().find('li');
	    var url_checkbox_arr = li_obj_arr.find('.url-checkbox'); //得到所有下载链接的checkbox
	    
	    var modalToolTitle = new Array();
	    var ed2kList = new Array();
	    var thunderList = new Array();
	    var magnetList = new Array();
	    url_checkbox_arr.each(function(c) {
	        if ($(this).prop('checked')) {
	            var download_btn = $(this).parent().next('ul.download-btn');
	            download_btn.find('a').each(function(i) {
	                var ahref = $(this).attr('href');
	                if (ahref.indexOf('ed2k') != -1) {
	                    ed2kList[c] = ahref;
	                } else if (ahref.indexOf('thunder') != -1) {
	                    thunderList[c] = ahref;
	                } else if (ahref.indexOf('magnet') != -1) {
	                    magnetList[c] = ahref;
	                } else thunderList[c] = ahref;
	                
	                if (typeof modalToolTitle[i] == 'undefined') {
	                    if (ahref.indexOf('ed2k') != -1) {
	                        modalToolTitle[i] = 'ed2k';
	                    } else if (ahref.indexOf('thunder') != -1) {
	                        modalToolTitle[i] = 'thunder';
	                    } else if (ahref.indexOf('magnet') != -1) {
	                        modalToolTitle[i] = 'magnet';
	                    } else modalToolTitle[i] = 'thunder';
	                }
	            });
	        }
	    });
	    
	    var toolsUl = $('#batch-download-model .modal-header').find('.tools .order');
	    var modalBodyObj = $('#batch-download-model .modal-body');
	    modalBodyObj.html('');
	    toolsUl.html('');
	    var downloadUrlList = new Array();
	    for (var i = 0; i < modalToolTitle.length; i++) {
	        var liClass = 'first active';
	        var liText = '', liType = '';
	        if (modalToolTitle[i].indexOf('ed2k') != -1) {
	            liText = '电驴链接';
	            liType = 'ed2k';
	            downloadUrlList = ed2kList;
	        } else if (modalToolTitle[i].indexOf('thunder') != -1) {
	            liText = '迅雷下载';
	            liType = 'thunder';
	            downloadUrlList = thunderList;
	        } else if (modalToolTitle[i].indexOf('magnet') != -1) {
	            liText = '磁力链接';
	            liType = 'magnet';
	            downloadUrlList = magnetList;
	        } else {
	            liText = '迅雷下载';
	            liType = 'thunder';
	            downloadUrlList = thunderList;
	        }
	        if (i > 0) liClass = 'middle-r';
	        if (modalToolTitle.length > 1) {
	            if (i == modalToolTitle.length - 1) liClass = 'last';
	        }
	        var liHtml = '<li type-name="' + liType + '" class="' + liClass + '"><a href="javascript:;" title="' + liText + '">' + liText + '</a></li>';
	        toolsUl.html(toolsUl.html() + liHtml);
	        
	        var downloadUrlText = '';
	        for (var u = 0; u < downloadUrlList.length; u++) {
	            if (typeof downloadUrlList[u] != 'undefined')
	                downloadUrlText = downloadUrlText + downloadUrlList[u] + '\r\n';
	        }
	        var hid = '';
	        if (i > 0) hid = 'style="display:none"';
	        modalBodyObj.html(modalBodyObj.html() + '<textarea type-name="' + liType + '" class="download-txt-list"' + hid + '>' + downloadUrlText + '</textarea>');
	        
	        $('#batch-download-model textarea').on('focus', function() {
	            $(this).select();
	        })
	    }
	    
	    var h = $(window).height() - 230;
	    $('#batch-download-model textarea').css('height', h);
	    $('#batch-download-model .modal-dialog').css('margin-top', ($(window).height() - h - 120) / 2);
	    $('#batch-download-model').modal();
	    
	    var toolsOrderLi = $('#batch-download-model .modal-header').find('.tools .order li');
	    
	    toolsOrderLi.click(function() {
	        toolsOrderLi.removeClass('active');
	        $(this).addClass('active');
	        var type_name = $(this).attr('type-name');
	        $('#batch-download-model .modal-body textarea').hide();
	        $('#batch-download-model .modal-body textarea[type-name=' + type_name + ']').show();
	    });
	});
})