/*
 * Copyright (c) 2021-2031, 河北计全科技有限公司 (https://www.jeequan.com & jeequan@126.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jeequan.jeepay.mch.ctrl.order;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jeequan.jeepay.core.constants.ApiCodeEnum;
import com.jeequan.jeepay.core.entity.TransferOrder;
import com.jeequan.jeepay.core.model.ApiPageRes;
import com.jeequan.jeepay.core.model.ApiRes;
import com.jeequan.jeepay.mch.ctrl.CommonCtrl;
import com.jeequan.jeepay.service.impl.TransferOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 转账订单api
 *
 * @author terrfly
 * @site https://www.jeequan.com
 * @date 2021/8/13 10:52
 */
@Api(tags = "订单管理（转账类）")
@RestController
@RequestMapping("/api/transferOrders")
public class TransferOrderController extends CommonCtrl {

    @Autowired
    private TransferOrderService transferOrderService;

    /**
     * list
     **/
    @ApiOperation("转账订单信息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "iToken", value = "用户身份凭证", required = true, paramType = "header"),
            @ApiImplicitParam(name = "pageNumber", value = "分页页码", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "分页条数", dataType = "int", defaultValue = "20"),
            @ApiImplicitParam(name = "createdStart", value = "日期格式字符串（yyyy-MM-dd HH:mm:ss），时间范围查询--开始时间，查询范围：大于等于此时间"),
            @ApiImplicitParam(name = "createdEnd", value = "日期格式字符串（yyyy-MM-dd HH:mm:ss），时间范围查询--结束时间，查询范围：小于等于此时间"),
            @ApiImplicitParam(name = "unionOrderId", value = "转账/商户/渠道订单号"),
            @ApiImplicitParam(name = "appId", value = "应用ID"),
            @ApiImplicitParam(name = "state", value = "支付状态: 0-订单生成, 1-转账中, 2-转账成功, 3-转账失败, 4-订单关闭", dataType = "Byte")
    })
    @PreAuthorize("hasAuthority('ENT_TRANSFER_ORDER_LIST')")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ApiPageRes<TransferOrder> list() {

        TransferOrder transferOrder = getObject(TransferOrder.class);
        JSONObject paramJSON = getReqParamJSON();
        LambdaQueryWrapper<TransferOrder> wrapper = TransferOrder.gw();
        wrapper.eq(TransferOrder::getMchNo, getCurrentMchNo());
        IPage<TransferOrder> pages = transferOrderService.pageList(getIPage(), wrapper, transferOrder, paramJSON);

        return ApiPageRes.pages(pages);
    }

    /**
     * detail
     **/
    @ApiOperation("转账订单信息详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "iToken", value = "用户身份凭证", required = true, paramType = "header"),
            @ApiImplicitParam(name = "recordId", value = "转账订单号", required = true)
    })
    @PreAuthorize("hasAuthority('ENT_TRANSFER_ORDER_VIEW')")
    @RequestMapping(value = "/{recordId}", method = RequestMethod.GET)
    public ApiRes detail(@PathVariable("recordId") String transferId) {
        TransferOrder refundOrder = transferOrderService.queryMchOrder(getCurrentMchNo(), null, transferId);
        if (refundOrder == null) {
            return ApiRes.fail(ApiCodeEnum.SYS_OPERATION_FAIL_SELETE);
        }
        return ApiRes.ok(refundOrder);
    }
}
