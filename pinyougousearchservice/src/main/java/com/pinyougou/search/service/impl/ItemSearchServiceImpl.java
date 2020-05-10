package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {



    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map search(Map searchMap) {
        Map map = new HashMap();

        String keywords = (String) searchMap.get("keyword");
        searchMap.put("keyword",keywords.replace(" ",""));

        map.putAll(searchList(searchMap));

        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);

        String category = (String) searchMap.get("category");
        if (!category.equals("")){
            map.putAll(searchBrandandSpecList(category));
        }else {
            if (categoryList.size()>0){
                map.putAll(searchBrandandSpecList(categoryList.get(0)));
            }
        }

        return map;
    }

    @Override
    public void importList(List list) {

        solrTemplate.saveBeans(list);
        solrTemplate.commit();

    }

    @Override
    public void deleteByGoodsIds(List goodsIds) {

        SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIds);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    private Map searchList(Map searchMap) {
        Map map = new HashMap();

        HighlightQuery query = new SimpleHighlightQuery();

        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);

        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keyword"));
        query.addCriteria(criteria);

        if (!"".equals(searchMap.get("category"))){
            SimpleFilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        if (!"".equals(searchMap.get("brand"))){
            SimpleFilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        if (searchMap.get("spec")!=null){
            Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                SimpleFilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);

            }
        }

        if (!"".equals(searchMap.get("price"))){
            String priceStr = (String) searchMap.get("price");
            String[] price = priceStr.split("-");
            if (!price[0].equals("0")){
                SimpleFilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!price[1].equals("*")){
                SimpleFilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo==null){
            pageNo=1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize==null){
            pageSize=20;
        }

        query.setOffset((pageNo-1)*pageSize);
        query.setRows(pageSize);


        String sortValue = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");

        if (sortValue!=null&&!sortValue.equals("")){
            Sort sort =null;
            if (sortValue.equals("ASC")){
                sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
            }
            if (sortValue.equals("DESC")){
                sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
            }
            query.addSort(sort);
        }



        //高亮页对象
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //高亮入口集合(每条记录的高亮入口)
        List<HighlightEntry<TbItem>> entries = highlightPage.getHighlighted();
        for (HighlightEntry<TbItem> entry : entries) {
            //获取高亮列表(高亮域的个数)
            List<HighlightEntry.Highlight> entryHighlights = entry.getHighlights();
//            for (HighlightEntry.Highlight highlight : entryHighlights) {
//            每1条需要高亮的Item，拿到它需要高亮的所有域
//                List<String> snipplets = highlight.getSnipplets();
//                System.out.println(snipplets);
//            }
            if (entryHighlights.size()>0&&entryHighlights.get(0).getSnipplets().size()>0){
                TbItem item = entry.getEntity();
                item.setTitle(entryHighlights.get(0).getSnipplets().get(0));
            }
        }
        List<TbItem> itemList = highlightPage.getContent();

        map.put("rows",itemList);
        map.put("totalPages",highlightPage.getTotalPages());
        map.put("total",highlightPage.getTotalElements());
        return map;
    }

    private List<String> searchCategoryList(Map searchMap){
        List<String> list = new ArrayList<String>();
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keyword"));
        query.addCriteria(criteria);

        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);

        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");

        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        for (GroupEntry<TbItem> entry : content) {
            list.add(entry.getGroupValue());
        }
        return list;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    private Map searchBrandandSpecList(String category){

        HashMap map = new HashMap();

        Long templateId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        if (templateId!=null){
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(templateId);
            map.put("brandList",brandList);
            List specList = (List) redisTemplate.boundHashOps("specList").get(templateId);
            map.put("specList",specList);
        }


        return map;
    }
}
