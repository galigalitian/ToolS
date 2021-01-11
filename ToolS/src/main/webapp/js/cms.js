$(function() {
    /*** 爬取电影开始 ***/
    $('.sel-movie-type').change(function() {
        var $this = $(this);
        var movieType = $this.val();
        $('.movie-spider-type').hide();
        $('.' + movieType).show();
    });
    
    $('.sel-tv-type').change(function() {
        var $this = $(this);
        var tvType = $this.val();
        $('.tv-spider-type').hide();
        $('.' + tvType).show();
    });
    
    $('.sel-dm-type').change(function() {
        var $this = $(this);
        var dmType = $this.val();
        $('.dm-spider-type').hide();
        $('.' + dmType).show();
    });
    
    /*爬取电影*/
    $('.movie-spider-type .download-btn a').click(function() {
        var $this = $(this);
        var before_html = $this.html();
        var dataUrl = $this.attr('data-url');
        var crawl_type = $this.attr('crawl_type');
        var $crawl_url = $this.parents('.movie-spider-type').find('input[name=crawl_url]');
        var $page_num = $this.parents('.movie-spider-type').find('input[name=page_num]');
        var page_num = $page_num.val();
        var crawl_url = $crawl_url.val();
        $this.html('正在爬取...');
        $.ajax({
            url: dataUrl,
            data: {'crawl_type': crawl_type, 'crawl_url': crawl_url, 'page_num': page_num},
            type: 'post',
            success: function() {
                $this.html(before_html);
            }
        })
    });
    /*爬取剧集*/
    $('.tv-spider-type .download-btn a').click(function() {
        var $this = $(this);
        var before_html = $this.html();
        var dataUrl = $this.attr('data-url');
        var crawl_type = $this.attr('crawl_type');
        var $crawl_url = $this.parents('.tv-spider-type').find('input[name=crawl_url]');
        var $page_num = $this.parents('.tv-spider-type').find('input[name=page_num]');
        var page_num = $page_num.val();
        var crawl_url = $crawl_url.val();
        $this.html('正在爬取...');
        $.ajax({
            url: dataUrl,
            data: {'crawl_type': crawl_type, 'crawl_url': crawl_url, 'page_num': page_num},
            type: 'post',
            success: function() {
                $this.html(before_html);
            }
        })
    });
    /*爬取动漫*/
    $('.dm-spider-type .download-btn a').click(function() {
        var $this = $(this);
        var before_html = $this.html();
        var dataUrl = $this.attr('data-url');
        var crawl_type = $this.attr('crawl_type');
        var $crawl_url = $this.parents('.dm-spider-type').find('input[name=crawl_url]');
        var crawl_url = $crawl_url.val();
        $this.html('正在爬取...');
        $.ajax({
            url: dataUrl,
            data: {'crawl_type': crawl_type, 'crawl_url': crawl_url},
            type: 'post',
            success: function() {
                $this.html(before_html);
            }
        })
    });
    
    /*** 爬取电影结束 ***/
    /*** manualInputMovie 手动添加电影页开始 ***/
    $('.sel_translate').change(function() {
    	var m_tf = $(this).prev().val();
		var m_translate = $(this).val();
		if (m_translate == '自动') {
			var language = $('input[name=language]').val();
		  	var languageArr = language.split('/');
		  	if (languageArr.length > 0) {
	            var lg = $.trim(languageArr[0]);
	            $(this).parents('.info-row').find('.m_translate').val(m_tf + lg + '中字');
	        }
		} else
			$(this).parents('.info-row').find('.m_translate').val(m_tf + m_translate);
	});
    $('.sel_translate_1').change(function() {
    	var m_tf = $(this).val();
		var m_translate = $(this).next().val();
		if (m_translate == '自动') {
			var language = $('input[name=language]').val();
		  	var languageArr = language.split('/');
		  	if (languageArr.length > 0) {
	            var lg = $.trim(languageArr[0]);
	            $(this).parents('.info-row').find('.m_translate').val(m_tf + lg + '中字');
	        }
		} else
			$(this).parents('.info-row').find('.m_translate').val(m_tf + m_translate);
    });
    if ($('.ajaxInputPage').length > 0) {
        var m_id = $('input[name=m_id]').val();
        var other_id = $('input[name=other_id]').val();
        var douban_id = $('input[name=douban_id]').val();
        var IMDB_id = $('#IMDB_id').val();
        var param = {};
        if (other_id != null && other_id != '') param.other_id = other_id;
        if (m_id != null && m_id != '') param.m_id = m_id;
        if (IMDB_id != null && IMDB_id != '') param.IMDB_id = IMDB_id;
        if (douban_id != null && douban_id != '') param.douban_id = douban_id;
        $.ajax({
            url: '/cms/download/ajaxGetDownloadList',
            type: 'post',
            data: param,
            success: function(html) {
            	var $downloadUl = $('.download-list-html .download-ul');
            	if ($downloadUl.find('li').length <= 1) $downloadUl.html('');
                writeDownloadUl(html);
                selChecked(); //分组显示时所有的checkbox
            }
        });
        
        /*if (douban_url != '' || IMDB_id != '') {
        	var parseDoubanUrlParam = {};
        	if (douban_url != '') {
        		douban_url = decodeURIComponent(douban_url);
        		parseDoubanUrlParam.douban_url = douban_url;
        	}
        	if (IMDB_id != '') {
        		parseDoubanUrlParam.IMDB_id = IMDB_id;
        	}
            $('#sel-ok-modal').modal({
                backdrop: 'static',
                keyboard: false
            });
            ajaxParseDoubanUrl(parseDoubanUrlParam, function() {
                  $('.manual-model').show();
                  var douban_id = $('input[name=douban_id]').val();
                  var IMDB_id = $('input[name=IMDB_url]').val();
                  param.douban_id = douban_id;
                  param.IMDB_id = IMDB_id;
                  $.ajax({
                      url: '/cms/download/ajaxGetDownloadList',
                      type: 'post',
                      data: param,
                      success: function(html) {
                    	  var $downloadUl = $('.download-list-html .download-ul');
                      	  if ($downloadUl.find('li').length <= 1) $downloadUl.html('');
                          writeDownloadUl(html);
                          selChecked();
                          $('#sel-ok-modal').modal('hide');
                      }
                  });
                  //$('.download-ul').html('');
                  //$('input[name=m_id]').val('');
                  
                  //$('input[name=other_id]').val('');
            });
        } else {
            var modelValData = {'modelVal': 'manual-model', 'm_id': m_id};
            ajaxInputPage(modelValData);
        }*/
    }
    
    /*添加下载链接*/
    $('.download-add').click(function() {
        $('.add-new-download').removeAttr('add-nd');
        $('.edit-download-a').removeAttr('edit-nd');
        $.ajax({
            url: '/cms/download/ajaxAddDownload',
            type: 'post',
            success: function(html) {
                $('#ajax-modal').find('.modal-body').html(html);
                writeExplain();
                $('#ajax-modal').modal();
            }
        })
    });
    
    /*添加字幕*/
    $('.subtitle-add').click(function() {
        $('#subtitle-list .download-ul li').removeAttr('edit');
        $.ajax({
            url: '/cms/subtitle/ajaxUploadSubtitlePage',
            type: 'post',
            success: function(html) {
                $('#ajax-modal').find('.modal-body').html(html);
                $('#ajax-modal').modal();
            }
        })
    });
    
    $('.conver-img').click(function() {
        $('#upload-cover-image').modal();
    });
    
    
    /*文件上传封面图片*/
    $('#upload_file_image').click(function() {
        $('#upload_file_form').ajaxSubmit({
            success: function(msg) {
                $('.conver-img-add').hide();
                $('#cover-image').attr('src', 'http://www.yikedy.co/' + msg);
                $('#cover-image').attr('data-src', msg);
                $('input[name=cover]').val(msg);
                $('#upload-cover-image').modal('hide');
            }
        });
    });
    
    /*url上传封面图片*/
    $('#upload_url_image').click(function() {
        $('#upload_url_form').ajaxSubmit({
            success: function(msg) {
                $('.conver-img-add').hide();
                $('#cover-image').attr('src', 'http://www.yikedy.co/' + msg);
                $('#cover-image').attr('data-src', msg);
                $('input[name=cover]').val(msg);
                $('#upload-cover-image').modal('hide');
            }
        });
    });
    
    /*修改说明*/
    $('.edit-explain').click(function() {
        editExplain($('#explain-modal'));
        $('#explain-modal').modal('hide');
    });
    
    /*解析链接*/
    $('#search-other-modal .parse-url-btn').click(function() {
    	var $parseUrl = $('#search-other-modal .parse-url');
    	var parseUrl = $parseUrl.val();
    	$('.search-other-sort').find('li').removeClass('active');
    	$('.search-other-sort').find('li:first').addClass('active');
    	$('.search-other-ul').html('');
    	$('.search-other-selCount .selCount').html(0);
    	$('#treeListJSON').val('');
    	$('#selectIds').val('');
    	$.ajax({
    		//dataType: "jsonp",
			//jsonp: "callback",
	        //jsonpCallback:"success_jsonpCallback",
			url: '/cms/downloadUtils/parseUrlListToJson',
			data: {'parse_url': encodeURIComponent(parseUrl)},
			type: 'get',
			success: function(data) {
				var msgs = data.msgList;
				if (msgs.length == 0) {
					alert('解析错误');
					return false;
				}
				for (var i = 0; i < msgs.length; i++) {
					var msg = msgs[i];
					var treeListJSON = msg.treeList;
					var obj = eval(treeListJSON);
					var str = JSON.stringify(treeListJSON);
					$('#treeListJSON').val(str);
					var $searchOtherSort = $('.search-other-sort');
					writeSearchOtherUl(treeListJSON);
					$searchOtherSort.find('li:first').addClass('active');
				}
			}
    	});
    });
    
    /*解析出链接后排序*/
    $('.search-other-sort li').click(function() {
    	var $this = $(this);
    	var className = $this.attr('class');
    	var str = $('#treeListJSON').val();
		var treeListJSON = JSON.parse(str);
		
		if (className == 'orderScore') {
		} else if (className == 'orderSize') {
    		treeListJSON.sort(sortSize);
    	} else if (className == 'orderDatetime') {
    		treeListJSON.sort(sortDatetime);
    	}
		if (className == 'selM' || className == 'selP' || className == 'isChinese') {
			var clipTreeListJSON = [];
			for (var t = 0; t < treeListJSON.length; t++) {
				var downloadUrl = treeListJSON[t].downloadUrl;
				var title = treeListJSON[t].title;
				if (className == 'selM') {
					if (downloadUrl.indexOf('magnet') != -1) {
						clipTreeListJSON.push(treeListJSON[t]);
					}
				} else if (className == 'selP') {
					if (downloadUrl.indexOf('pan.baidu.com') != -1) {
						clipTreeListJSON.push(treeListJSON[t]);
					}
				} else if (className == 'isChinese') {
					if (isChinese(title)) {
						clipTreeListJSON.push(treeListJSON[t]);
					}
				}
			}
			writeSearchOtherUl(clipTreeListJSON);
		} else {
			writeSearchOtherUl(treeListJSON);
		}
    	$('.search-other-sort li.active').removeClass('active');
    	$this.addClass('active');
    });
    
    /*添加选中的链接*/
    $('.write-url-btn').click(function() {
    	var selectIds = $('#selectIds').val();
    	var selectIdsArr = selectIds.split(',');
    	var str = $('#treeListJSON').val();
		var treeListJSON = JSON.parse(str);
		var list_li_html = '';
		for (var t = 0; t < treeListJSON.length; t++) {
			var id = treeListJSON[t].id;
			var downloadUrl = treeListJSON[t].downloadUrl;
			var title = treeListJSON[t].title;
			var sizeStr = treeListJSON[t].sizeStr;
			if ($.inArray(id, selectIdsArr) != -1) {
				var downloadType = '';
				if (downloadUrl.indexOf('magnet') != -1) {
					downloadType = '磁力链接';
				} else if (downloadUrl.indexOf('pan.baidu.com') != -1) {
					downloadType = '网盘下载';
				} else if (downloadUrl.indexOf('thunder') != -1) {
					downloadType = '迅雷下载';
				}
				var downloadLiHtml = '<li class="clearfix">' +
                '    <div class="download-title">' +
                '        <label><input download_id="" checked="checked" type="checkbox" class="url-checkbox cms-checkbox g-' + id + '" gid="g-' + id + '">' +
                '        <span class="invalid">无效</span>' +
                '        <textarea style="height:37px" name="download_title" class="form-control text text500 float-left title " placeholder="下载标题" type="text">' + title + '</textarea>&nbsp;&nbsp;<span>' + sizeStr + '</span></label>' +
                '    </div>' +
                '    <ul class="download-btn clearfix">' +
                '    	<li>' +
                '			<a class="margin-right-0" href="' + downloadUrl + '" title="' + downloadType + '">' + downloadType + '</a><a class="edit-download-a margin-left-1 margin-right-0" href="javascript:;" title="修改链接"><i class="icon-edit"></i></a><a class="mius-download-a margin-left-1" href="javascript:;" title="删除链接"><i class="icon-minus-sign"></i></a>' +
                '           <input type="hidden" name="downloadId" value="">' +
                '           <input type="hidden" name="downloadTitle" value="' + title + '">' +
                '           <input type="hidden" name="downloadUrl" value="' + downloadUrl + '">' +
                '			<input type="hidden" name="downloadPassword" value="">' +
                '			<input type="hidden" name="downloadSize" value="">' +
                '		</li>' +
                '	</ul>' +
                '	<input type="hidden" name="downloadStatus" value="0">' +
                '   <input type="hidden" name="downloadGroupName" value="">' +
                '   <a class="add-new-download" href="javascript:;"><i class="icon-plus-sign"></i>增加链接</a> | <a class="edit-all-download" href="javascript:;" title="修改所有链接"><i class="icon-edit"></i>修改所有链接</a> | ' +
                '   <a class="del-all-download" href="javascript:;" title="删除所有链接包括该链接名称"><i class="icon-minus"></i>删除所有链接</a></li>' +
				'</li>';
				list_li_html = list_li_html + downloadLiHtml;
			}
		}
		writeDownloadUl(list_li_html);
		$('#search-other-modal').modal('hide');
    });
    /*** manualInputMovie 手动添加电影页结束 ***/
});

function success_jsonpCallback(data) {
}
function writeHiddenByClick() {
}

function sortSize(x, y) {
	return (x.size < y.size) ? 1 : -1
}
function sortDatetime(x, y) {
	return (x.dateTime < y.dateTime) ? 1 : -1
}
function sortScore(x, y) {
	return (x.score < y.score) ? 1 : -1
}

function writeSearchOtherUl(treeListJSON) {
	var $searchOtherUl = $('.search-other-ul');
	var liHtml = '';
	var selectIds = $('#selectIds').val();
	var selectIdsArr = new Array();
	if (selectIds != '') {
		selectIdsArr = selectIds.split(',');
	}
	for (var t = 0; t < treeListJSON.length; t++) {
		var titleName = treeListJSON[t].title; //treeList中的title
		var downloadUrl = treeListJSON[t].downloadUrl;
		var sizeStr = treeListJSON[t].sizeStr;
		var id = treeListJSON[t].id;
		var date = treeListJSON[t].date;
		var iconClass = '';
		if (downloadUrl.indexOf('magnet') != -1) {
			iconClass = 'icon-magnet';
		} else if (downloadUrl.indexOf('pan.baidu.com') != -1) {
			iconClass = 'icon-inbox';
		}
		var checked = '', selectedCss = '';
		if ($.inArray(id, selectIdsArr) != -1) {
			checked = ' checked="checked"';
			selectedCss = 'selected';
		}
		var html = '<li class="' + selectedCss + '">' +
		           '<input class="sel-search-one" id="' + id + '" type="checkbox"' + checked + '><label for="' + id + '"><i class="' + iconClass + '"></i>' +
		           '<div class="title-name" target="_blank">' + titleName + '</div></label>' +
		           '<span>' + sizeStr + '</span><br><span class="margin-left-20">' + date + '</span>&nbsp;' +
		           '<a href="' + downloadUrl + '" target="_blank">打开链接</a>' +
		           '</li>';
		liHtml = liHtml + html;
	}
	$searchOtherUl.html(liHtml);
	$('.sel-search-one').on('change', function() {
		if ($(this).prop('checked')) {
			$(this).parent().addClass('selected');
		} else {
			$(this).parent().removeClass('selected');
		}
		selCheckedUrl();
	});
}
function selCheckedUrl() { //循环得到已选中的链接checkbox's id
	var selectIdsArr = new Array();
	$('.search-other-ul li').find('input:checkbox').each(function() {
		if ($(this).prop('checked')) {
			selectIdsArr.push($(this).attr('id'));
		}
	});
	$('#selectIds').val(selectIdsArr.toString());
	$('.search-other-selCount span').html(selectIdsArr.length);
}
function isChinese(str) {
	if (/.*[\u4e00-\u9fa5]+.*/.test(str)) return true;
	else return false;
}
/*分组显示时所有的checkbox相关操作*/
function selChecked() {
	$('.all-sel').click(function() { //全选
    	var url_checkbox = $(this).parents('ul').find('.url-checkbox');
    	if ($(this).prop('checked'))
    		url_checkbox.prop('checked', true);
    	else url_checkbox.prop('checked', false);
    });
    $('.group-all-sel').click(function() { //分组全选
    	var gid = $(this).attr('allGid');
    	var url_checkbox = $(this).parents('ul').find('.' + gid);
    	if ($(this).prop('checked'))
    		url_checkbox.prop('checked', true);
    	else url_checkbox.prop('checked', false);
    	
    	allChecked($(this));
    });
    
    $('.cms-checkbox').click(function() { //每一个checkbox
    	var gid = $(this).attr('gid');
    	var isGAll = true;
    	$('.' + gid).each(function() {
    		if (!$(this).prop('checked')) {
    			$('input[allGid=' + gid + ']').prop('checked', false);
    			isGAll = false;
    			return false;
    		}
    	});
    	if (isGAll) $('input[allGid=' + gid + ']').prop('checked', true);
    	allChecked($(this));
    });
}

function allChecked($this) { //判断是否全部选中
	var isAll = true;
	$this.parents('ul').find('.url-checkbox').each(function() {
		if ($(this).attr('is_valid') == 'valid') {
	  		if (!$(this).prop('checked')) {
	  			$('.all-sel').prop('checked', false);
	  			isAll = false;
	  			return false;
	  		}
		}
  	});
  	if (isAll) $('.all-sel').prop('checked', true);
}
function editExplain($modal) {
    var m_version = $modal.find('.m_version').val();
    var m_version_txt = '';
    if (m_version != '')
        m_version_txt = $modal.find('.m_version').find("option:selected").text();
    var m_dpi = $modal.find('.m_dpi').val();
    var m_dpi_txt = '';
    if (m_dpi != '')
        m_dpi_txt = $modal.find('.m_dpi').find("option:selected").text();
    var m_translate = $modal.find('.m_translate').val();
    var m_explain = $modal.find('.m_explain').val();
    var fTxt = m_version_txt + m_dpi_txt;
    $('input[name=m_version]').val(m_version);
    $('input[name=m_dpi]').val(m_dpi);
    $('input[name=m_translate]').val(m_translate);
    $('input[name=m_explain]').val(m_explain);
    fTxt = fTxt != '' ? ('[' + fTxt + ']') : '';
    m_translate = m_translate != '' ? ('[' + m_translate + ']') : '';
    m_explain = m_explain != '' ? ('[' + m_explain + ']') : '';
    $('.explain').html(fTxt + m_translate + m_explain);
}

function writeExplain() {
    var m_version = $('input[name=m_version]').val();
    var m_dpi = $('input[name=m_dpi]').val();
    var m_translate = $('input[name=m_translate]').val();
    var m_explain = $('input[name=m_explain]').val();
    $('.m_version').val(m_version);
    $('.m_dpi').val(m_dpi);
    $('.m_translate').val(m_translate);
    $('.m_explain').val(m_explain);
    var weiboCid = $('input[name=weiboCid]').val();
    if (weiboCid != '') {
    	var weiboUname = $('input[name=weiboUname]').val();
    	$('.weibo_uinfo').show();
    	$('.weibo_uname').val(weiboUname);
    }
}

function ajaxInputPage(data) {
    $.post('/cms/manual/ajaxInputPage', data, function(html) {
        $('.ajaxInputPage').html(html);
        var m_id = $('input[name=m_id]').val();
       	ajaxOtherMovie(m_id); //ajax得到爬取电影的信息
        var cover = $('input[name=cover]').val();
        if (cover != '') {
            $('.conver-img-add').hide();
            $('#cover-image').attr('src', 'http://www.yikedy.co/' + cover);
        }
        $('.m_name_href').html($('input[name=c_name]').val());
        $.ajax({
            url: '/cms/download/ajaxGetDownloadList',
            type: 'post',
            data: {'m_id': m_id},
            success: function(html) {
                writeDownloadUl(html);
                selChecked();
            }
        });
    });
}
function ajaxOtherMovie(m_id) {
    if ($('.other-movie').find('input[name=om_id]').length > 0 && $('.other-movie').find('input[name=om_id]').val() != '') {
		$('.ajaxInputPage').find('input[name=c_name]').val($('.other-movie').find('input[name=om_name]').val());
        $('.ajaxInputPage').find('input[name=douban_url]').val($('.other-movie').find('input[name=om_doubanurl]').val());
    } else {
        var other_id = $('input[name=other_id]').val();
        if (other_id != '') {
            $.ajax({
                url: '/cms/other/ajaxGetSimpleOtherMovieDetail',
                data: {'other_id': other_id},
                type: 'post',
                success: function(html) {
                    $('.other-movie').html(html);
                    //$('.ajaxInputPage').find('input[name=c_name]').val($('.other-movie').find('input[name=om_name]').val());
                }
            })
        }
    }
}
/*function writeData(data) {
    var cover = ''
    if (data.cover != null && typeof data.cover != 'undefined') {
        cover = "https://images.weserv.nl/?url=" + data.cover;
        $('input[name=cover]').val(data.cover);
    } else {
        cover = 'http://www.yikedy.co/' + data.cover_image;
        $('input[name=cover]').val(data.cover_image);
    }
    if (cover != '') {
        $('.conver-img-add').hide();
        $('#cover-image').attr('src', cover);
    }
    $('input[name=c_name]').val(data.c_name);
    $('input[name=e_name]').val(data.e_name);
    $('input[name=year]').val(data.year);
    $('input[name=other_name]').val(data.other_name);
    $('input[name=director]').val(data.director);
    $('input[name=actors]').val(data.actors);
    $('input[name=scriptwriter]').val(data.scriptwriter);
    $('input[name=movie_type]').val(data.movie_type);
    $('input[name=language]').val(data.language);
    $('input[name=make_area]').val(data.make_area);
    $('input[name=show_time]').val(data.show_time);
    $('input[name=show_date]').val(data.show_date);
    $('input[name=rate]').val(data.rate);
    $('textarea[name=about]').val(data.about);
    var part_count = '', session = '';
    if (data.part_count != null && typeof data.part_count != 'undefined')
        part_count = data.part_count;
    if (data.session != null && typeof data.session != 'undefined')
        session = data.session;
    $('input[name=part_count]').val(part_count);
    $('input[name=session]').val(session);
    if (part_count != '' || session != '')
           $('.tv-info-row').show();
    var douban_url = data.douban_url;
    if (douban_url != null && typeof douban_url != 'undefined') {
        $('.doubanUrl-model').show();
        $('.sel-model').val(2);
    }
    $('input[name=douban_url]').val(douban_url);
}*/
/*解析豆瓣链接方法*/
function ajaxParseDoubanUrl(param, callback) {
	param.modelVal = 'doubanUrl-model';
    $.ajax({
        url: '/cms/manual/parseDoubanUrl',
        type: 'post',
        data: param,
        success: function(data) {
            $('.ajaxInputPage').html(data);
            
            var cover = $('input[name=cover]').val();
            if (cover != '') {
                $('.conver-img-add').hide();
                $('#cover-image').attr('src', "https://images.weserv.nl/?url=" + cover);
            }
            //writeData(data);
            //$this.html(before_html);
            //$('.manual-model').show();
            if (callback)
                callback();
        }
    });
}
function parseDoubanUrl($this, callback) {
    var douban_url = $this.parents('.doubanUrl-model').find('input[name=douban_url]').val();
    if (douban_url == '') {
        alert('豆瓣链接不能为空');
        return false;
    }
    var before_html = $this.html();
    var after_html = '正在解析...';
    $this.html(after_html);
    $this.unbind('click');
    var param = {'douban_url': douban_url};
    ajaxParseDoubanUrl(param, callback);
}

function writeDownloadUl(list_li_html, target_download_btn, add_cover) {
    
    var $downloadUl = null;
    if (target_download_btn != null) $downloadUl = target_download_btn;
    else $downloadUl = $('.download-list-html .download-ul');
    $downloadUl.find('.mius-download-a').off('cilck');
    if (typeof add_cover != 'undefined')
    	$downloadUl.html(list_li_html);
    else
    	$downloadUl.append(list_li_html);
    $downloadUl.find('.mius-download-a').on('click', function() { //删除每一个链接
        var $this = $(this);
        if (window.confirm("是否删除链接")) {
            var $downloadId = $this.parent('li').find('input[name=downloadId]');
            if ($downloadId != null && $downloadId.length > 0 && $downloadId.val() != '') {
                var downloadId = $downloadId.val();
                $.ajax({
                    url: '/cms/download/ajaxDeleteDownload',
                    type: 'post',
                    data: {'downloadId': downloadId},
                    success: function(data) {
                        if (data == 1) $this.parent('li').remove();
                    }
                })
            } else {
                $this.parent('li').remove();
            }
//            if ($(this).prev('a').prev('span').length > 0) {
//                $(this).prev('a').prev('span').prev('a').remove();
//                $(this).prev('a').prev('span').remove();
//            } else $(this).prev('a').prev('a').remove();
//            $(this).prev('a').remove();
//            $(this).remove();
        }
    });
    
    if (target_download_btn == null) {
        $('.del-all-download').off('click');
        $('.add-new-download').off('click');
        $('.del-all-download').on('click', function() { //删除该标题下的所有链接
            if (window.confirm("是否要删除该标题下的所有链接以及标题")) {
                
                $(this).parent().remove();
            }
        });
        $('.add-new-download').on('click', function() { //新增该标题下的一个链接
            $('.add-new-download').removeAttr('add-nd');
            $('.edit-download-a').removeAttr('edit-nd');
            $(this).attr('add-nd', 1);
            var $this = $(this);
            $.ajax({
                url: '/cms/download/ajaxAddDownload',
                type: 'post',
                success: function(html) {
                    $('#ajax-modal').find('.modal-body').html(html);
                    $('.urlType-op').hide();
                    $('.download-url-type').find('input[name=download_size]').hide();
                    $('.download-pan-type').find('input[name=download_size]').hide();
                    var title_a_obj = $this.parent().find('div.download-title').find('a');
                    var title_t_obj = $this.parent().find('div.download-title').find('textarea[name=download_title]');
                    var downloadTitle = '';
                    if (title_a_obj.length > 0) downloadTitle = title_a_obj.html();
                    else if (title_t_obj.length > 0) downloadTitle = title_t_obj.val();
                    $('.select-download-model').val(2);
                    $('.download-url-type').show();
                    $('.download-url-type .download-titles-ul .download-titles-li:first').find('input[name=download_title]').val(downloadTitle);
                    $('.download-pan-type .download-titles-ul .download-titles-li:first').find('input[name=download_title]').val(downloadTitle);
                    writeExplain();
                    $('#ajax-modal').modal();
                }
            });
        });
    }
    editDownload();
}

function editDownload() {
    $('.edit-download-a').off('click');
    $('.edit-download-a').on('click', function() {
        $('.add-new-download').removeAttr('add-nd');
        $('.edit-download-a').removeAttr('edit-nd');
        $(this).attr('edit-nd', 1);
        var $thisparent = $(this).parent('li');
        
        //$(this).parents('li').find('.add-new-download').attr('edit-nd', 1);
        var $this = $(this);
        $.ajax({
            url: '/cms/download/ajaxAddDownload',
            type: 'post',
            success: function(html) {
                $('#ajax-modal').find('.modal-body').html(html);
                var $downloadMt = null;
                var downloadUrl = '', downloadPassword = '';
                if ($this.prev('span').length > 0 || $this.prev('a').html() == '网盘下载') { //网盘模式
                    $('.select-download-model').val(3);
                    $('.select-download-model').attr('disabled', 'disabled');
                    $('.download-pan-type').show();
                    $downloadMt = $('.download-pan-type');
                    downloadUrl = $this.prev('span').prev('a').attr('href');
                    downloadPassword = $this.prev('span').html();
                } else {
                    $('.select-download-model').val(2);
                    $('.select-download-model').attr('disabled', 'disabled');
                    $('.download-url-type').show();
                    $downloadMt = $('.download-url-type');
                    downloadUrl = $this.prev().attr('href');
                }
                var title_a_obj = $this.parents('li').find('div.download-title').find('a');
                var title_t_obj = $this.parents('li').find('div.download-title').find('textarea[name=download_title]');
                var $downloadTitle = null;
                var downloadTitleTxt = '';
                if (title_a_obj.length > 0) {
                	$downloadTitle = title_a_obj;
                	downloadTitleTxt = $downloadTitle.html();
                } else if (title_t_obj.length > 0) {
                	$downloadTitle = title_t_obj;
                	downloadTitleTxt = $downloadTitle.val();
                }
                var downloadSize = $downloadTitle.next('span').html();
                
                var downloadTitlesLi = $downloadMt.find('.download-titles-ul .download-titles-li:first');
                downloadTitlesLi.find('input[name=download_title]:first').val(downloadTitleTxt);
                downloadTitlesLi.find('input[name=download_size]:first').val(downloadSize);
                var $downloadUrlFirst = downloadTitlesLi.find('.download-urls-ul').find('input[name=download_url]:first');
                $downloadUrlFirst.val(downloadUrl);
                if ($downloadUrlFirst.next('input[name=download_password]').length > 0)
                    $downloadUrlFirst.next('input[name=download_password]').val(downloadPassword);
                downloadTitlesLi.find('.download-urls-ul').find('a').hide();
                downloadTitlesLi.find('.urlType-op').hide();
                $('.save-btn').find('a').html('修改链接');
                writeExplain();
                $('#ajax-modal').modal();
            }
        })
    });
}

function subtitle() {
    $('.edit-subtitle').off('click');
    $('.edit-subtitle').on('click', function() {
        $('#subtitle-list .download-ul li').removeAttr('edit');
        var $thisparent = $(this).parent('li');
        $.ajax({
            url: '/cms/subtitle/ajaxUploadSubtitlePage',
            type: 'post',
            success: function(html) {
                $('#ajax-modal').find('.modal-body').html(html);
                
                
                var $subtitleUrl = $thisparent.find('input[name=subtitleUrl]'); //原始链接
                var $subtitleUploadUrl = $thisparent.find('input[name=subtitleUploadUrl]'); //下载链接
                var $subtitleName = $thisparent.find('input[name=subtitleName]');
                var $subtitleSource = $thisparent.find('input[name=subtitleSource]');
                
                var subtitleUrl = $subtitleUrl.val();
                var subtitleUploadUrl = $subtitleUploadUrl.val();
                
                if (subtitleUploadUrl.indexOf('http') == 0) {
                	$subtitleUrl.val(subtitleUploadUrl);
                }
                
                $('input[name=subtitle_name]').val($subtitleName.val());
                $('input[name=subtitle_url]').val($subtitleUrl.val());
                $('input[name=subtitle_upload_url]').val($subtitleUploadUrl.val());
                $('input[name=subtitle_source]').val($subtitleSource.val());
                var subtitleDes = $thisparent.find('input[name=subtitleDes]').val();
                $('#selSubtitleDes').val(subtitleDes);
                $thisparent.attr('edit', 1);
                $('#ajax-modal').modal();
            }
        });
    });
    
    $('.add-new-subtitle').off('click');
    $('.add-new-subtitle').on('click', function() {
        $('#subtitle-list .download-ul li').removeAttr('edit');
        $.ajax({
            url: '/cms/subtitle/ajaxUploadSubtitlePage',
            type: 'post',
            success: function(html) {
                $('#ajax-modal').find('.modal-body').html(html);
                $('#ajax-modal').modal();
            }
        });
    });
    
    
    $('.del-subtitle').off('click');
    $('.del-subtitle').on('click', function() {
        var b = window.confirm('是否删除字幕');
        if (b) {
            var $thisparent = $(this).parent('li');
            var $subtitleId = $thisparent.find('input[name=subtitleId]');
            if ($subtitleId.length > 0 && $subtitleId.val() != '') {
            	var subtitleId = $subtitleId.val();
            	$.post('/cms/subtitle/deleteMovieSubtitle', {'subtitleId': subtitleId}, function() {
            		$thisparent.remove();
            	});
            } else {
                $thisparent.remove();
            }
        }
    });
    
    $('.sub-all-sel').click(function() { //全选字幕
    	var url_checkbox = $(this).parents('ul').find('.url-checkbox');
      	if ($(this).prop('checked'))
      		url_checkbox.prop('checked', true);
      	else url_checkbox.prop('checked', false);
    });
    
    $('.cms-sub-checkbox').click(function() { //选中每一个字幕
    	var isAll = true;
      	$(this).parents('ul').find('.cms-sub-checkbox').each(function() {
      		if (!$(this).prop('checked')) {
      			$('.sub-all-sel').prop('checked', false);
      			isAll = false;
      			return false;
      		}
      	});
      	if (isAll) $('.sub-all-sel').prop('checked', true);
    });
}

