package com.ellenfang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.entity.Link;
import com.ellenfang.domain.vo.LinkVo;
import com.ellenfang.domain.vo.PageVo;


/**
 * 友链(Link)表服务接口
 *
 * @author makejava
 * @since 2022-07-13 16:22:57
 */
public interface LinkService extends IService<Link> {

    ResponseResult getAllLink();

    ResponseResult<PageVo> getAllLinkByPage(Integer pageNum, Integer pageSize, String name, String status);

    ResponseResult addLink(Link link);

    ResponseResult<LinkVo> queryLinkById(Long id);

    ResponseResult updateLink(Link link);

    ResponseResult deleteLink(Long id);
}

