package com.ellenfang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ellenfang.constants.SystemConstants;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.entity.Link;
import com.ellenfang.domain.vo.LinkVo;
import com.ellenfang.domain.vo.PageVo;
import com.ellenfang.mapper.LinkMapper;
import com.ellenfang.service.LinkService;
import com.ellenfang.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 友链(Link)表服务实现类
 *
 * @author makejava
 * @since 2022-07-13 16:22:57
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    @Override
    public ResponseResult getAllLink() {
        // 查询所有审核通过的友链
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getStatus, SystemConstants.LINK_STATUS_NORMAL);
        List<Link> links = list(queryWrapper);

        // 转换 vo
        List<LinkVo> linkVos = BeanCopyUtils.copyBeanList(links, LinkVo.class);

        // 封装返回
        return ResponseResult.okResult(linkVos);
    }

    @Override
    public ResponseResult<PageVo> getAllLinkByPage(Integer pageNum, Integer pageSize, String name, String status) {
        // 根据相关条件进行查询
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        // 若参数：name 不为空，则按照友链名称进行模糊查询
        queryWrapper.like(StringUtils.hasText(name), Link::getName, name);
        // 若参数：status 不为空，则按照状态进行查询
        queryWrapper.eq(StringUtils.hasText(status), Link::getStatus, status);

        // 分页查询
        Page<Link> page = new Page(pageNum, pageSize);
        page(page, queryWrapper);
        List<Link> links = page.getRecords();

        // 封装为 vo
        List<LinkVo> linkVos = BeanCopyUtils.copyBeanList(links, LinkVo.class);
        PageVo pageVo = new PageVo(linkVos, page.getTotal());

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult addLink(Link link) {
        save(link);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult<LinkVo> queryLinkById(Long id) {
        // 根据 id 查询link
        LinkMapper linkMapper = getBaseMapper();
        Link link = linkMapper.selectById(id);
        // 转换为 vo
        LinkVo linkVo = BeanCopyUtils.copyBean(link, LinkVo.class);
        return ResponseResult.okResult(linkVo);
    }

    @Override
    public ResponseResult updateLink(Link link) {
        updateById(link);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteLink(Long id) {
        removeById(id);
        return ResponseResult.okResult();
    }
}

