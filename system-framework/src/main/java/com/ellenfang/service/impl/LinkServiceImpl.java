package com.ellenfang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ellenfang.constants.SystemConstants;
import com.ellenfang.domain.ResponseResult;
import com.ellenfang.domain.entity.Link;
import com.ellenfang.domain.vo.LinkVo;
import com.ellenfang.mapper.LinkMapper;
import com.ellenfang.service.LinkService;
import com.ellenfang.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;

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
}

