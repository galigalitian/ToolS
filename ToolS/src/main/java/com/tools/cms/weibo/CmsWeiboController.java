package com.tools.cms.weibo;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tools.common.ListPage;
import com.tools.model.WeiboUser;
import com.tools.utils.DateUtils;
import com.tools.weibo.controller.WeiboUtilsController;

@Controller
@RequestMapping("/cms/weibo")
public class CmsWeiboController extends WeiboUtilsController {

    @Autowired
    private MongoTemplate mongoTemplate;
    
    /**
     * 得到微博信息列表
     * @param request
     * @param response
     * @param modelMap
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping("/getWeiboContentList")
    public String getWeiboContentList(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        Integer p = request.getParameter("p") != null ? Integer.parseInt(request.getParameter("p")) - 1 : 0;
        int rowNum = 30;
        int firstRow = p * rowNum;
        String orderCol = request.getParameter("orderCol");
        String order = request.getParameter("order");
        String userId = request.getParameter("userId");
        String searchQuery = request.getParameter("searchQuery");
        Query query = new Query();
        if (request.getParameter("isAdd") != null) {
            Integer isAdd = Integer.parseInt(request.getParameter("isAdd"));
            query.addCriteria(Criteria.where("isAdd").is(isAdd));
            modelMap.addAttribute("isAdd", isAdd);
        }
        /*if (StringUtils.isNotBlank(userUrl)) {
            query.addCriteria(Criteria.where("userStr").is(userUrl));
        }*/
        if (StringUtils.isNotBlank(userId)) {
            /*Query userQuery = new Query();
            userQuery.addCriteria(Criteria.where("_id").is(userId));
            WeiboUser weiboUser = mongoTemplate.findOne(userQuery, WeiboUser.class);
            userUrl = weiboUser.getUserUrl();*/
            query.addCriteria(Criteria.where("userId").is(userId));
        }
        if (StringUtils.isNotBlank(searchQuery)) {
            if (NumberUtils.isCreatable(searchQuery)) {
                query.addCriteria(Criteria.where("weiboId").is(searchQuery));
            } else {
                Pattern pattern = Pattern.compile(".*" + searchQuery + ".*", Pattern.CASE_INSENSITIVE);
                query.addCriteria(Criteria.where("content").regex(pattern));
            }
        }
        long allRow = mongoTemplate.count(query, Map.class, "weibo_content");
        Sort sort = Sort.by(StringUtils.isNotBlank(order) && order.equals("asc") ? Direction.ASC : Direction.DESC,
                StringUtils.isNotBlank(orderCol) ? orderCol : "createTime");
        Pageable pageable = PageRequest.of(p, 30, sort);
        List<Map> weiboContentList = mongoTemplate.find(query.with(pageable), Map.class, "weibo_content");
        for (Map weiboContentMap : weiboContentList) {
            weiboContentMap.put("weiboDetailTime", DateUtils.format(Long.parseLong(weiboContentMap.get("weiboDetailTime").toString()), DateUtils.timePattern));
            weiboContentMap.put("createTime", DateUtils.format(Long.parseLong(weiboContentMap.get("createTime").toString()), DateUtils.timePattern));
            weiboContentMap.put("updateTime", DateUtils.format(Long.parseLong(weiboContentMap.get("updateTime").toString()), DateUtils.timePattern));
        }
        ListPage<Map> listPage = new ListPage<>(weiboContentList, firstRow, rowNum, new Long(allRow).intValue());
        modelMap.addAttribute("weiboContentListPage", listPage);
        modelMap.addAttribute("weiboUserList", getWeiboUserList());
        modelMap.addAttribute("userId", userId);
        modelMap.addAttribute("orderCol", orderCol);
        modelMap.addAttribute("order", order);
        return "/cms/weibo/getWeiboContentList";
    }
    
    /**
     * 删除微博信息
     * @param request
     * @param response
     * @param modelMap
     * @param id
     */
    @RequestMapping("/deleteWeiboComment")
    @ResponseBody
    public int deleteWeiboComment(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, String id) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("isAdd", -1);
        //mongoTemplate.updateFirst(query, update, WeiboComment.class);
        return 1;
    }
    
    private List<WeiboUser> getWeiboUserList() {
//        AggregationResults<WeiboUser> aggregationResults = mongoTemplate.aggregate(
//                Aggregation.newAggregation(
//                        Aggregation.project("userStr", "userUrl", "_id"),
//                        Aggregation.group("_id", "userStr").first("userStr").as("userStr").first("userUrl").as("userUrl"),
//                        Aggregation.project("userStr", "userUrl", "_id"))
//                .withOptions(Aggregation.newAggregationOptions().build()), "weiboUser", WeiboUser.class);
//        @SuppressWarnings("unchecked")
//        List<WeiboUser> weiboUserList = IteratorUtils.toList(aggregationResults.iterator());
        List<WeiboUser> weiboUserList = mongoTemplate.find(new Query().with(Sort.by(Direction.DESC, "create_time")), WeiboUser.class);
        return weiboUserList;
    }
}
