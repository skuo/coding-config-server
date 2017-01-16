package com.coding.configserver.rest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coding.configserver.dto.BidDto;
import com.coding.configserver.dto.BidStatus;
import com.coding.configserver.service.BidService;

import io.swagger.annotations.ApiOperation;

@Controller
public class BidController {
    private static final Log log = LogFactory.getLog(BidController.class);

    @Autowired
    BidService bidService;

    @RequestMapping(method = RequestMethod.GET, value = "/version", headers = "accept=application/json")
    @ResponseBody
    public String getVersion(HttpServletRequest request, HttpServletResponse response) {
        // log.info("getVersion");
        StringBuilder sb = new StringBuilder();
        sb.append("{\"java.version\":\"" + System.getProperty("java.version") + "\"}");
        log.info(sb.toString());
        return sb.toString();
    }

    @ApiOperation(value = "getBid", nickname = "getBid")
    @RequestMapping(method = RequestMethod.GET, value = "/bids/{sourceId}/source/{source}", headers = "accept=application/json")
    @ResponseBody
    /**
     * Return a list of BidDto in json format and status code of 200, 404 and 500.
     * 
     * @param sourceId
     *            original source id
     * @param source
     *            source identifier
     * @param response
     *            HttpServletReponse
     * @return BidDtos in json format
     */
    public List<BidDto> getBids(@PathVariable String sourceId, @PathVariable String source, HttpServletRequest request,
            HttpServletResponse response) {
        log.info("getBid: sourceId=" + sourceId + ", source=" + source + ", accept=" + request.getHeader("Content-Type")
                + ", Authorization=" + request.getHeader("Authorization"));
        List<BidDto> bidDtos = bidService.getBids(sourceId, source);
        response.setHeader("Approved", "true");
        return bidDtos;
    }

    @ApiOperation(value = "putBid", nickname = "putBid")
    @RequestMapping(method = RequestMethod.PUT, value = "/bids/{sourceId}/source/{source}", headers = "accept=application/json")
    @ResponseBody
    /**
     * Put bid. Returns BidStatus with status code of 200 or 500.
     * 
     * @param sourceId
     *            original source id
     * @param source
     *            source identifier
     * @param bid
     *            Bid
     * @param response
     *            HttpServletReponse
     * @return boolean
     */
    public BidStatus putBid(@PathVariable String sourceId, @PathVariable String source, @RequestBody BidDto bidDto,
            HttpServletResponse response) throws SQLException {
        log.info("putBid: sourceId=" + sourceId + ", source=" + source + ", bidDto=" + bidDto.toString());
        BidStatus bidStatus = new BidStatus(BidStatus.SUCCESS, "");
        bidDto.setSourceId(sourceId);
        bidDto.setSource(source);
        boolean status = bidService.saveBid(bidDto);
        if (!status) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            bidStatus.setStatus(BidStatus.FAILURE);
        }
        return bidStatus;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/bids/{sourceId}", headers = "accept=application/json", produces = "application/json")
    @ResponseBody
    /**
     * Return a BidDto in json format and status code of 200, 404 and 500.
     * 
     * @param sourceId
     *            original source id
     * @param source
     *            source identifier
     * @param id
     *            optional request param, not used.
     * @param response
     *            HttpServletResponse
     * @return BidStatus in json format
     */
    public BidDto getBidByPathAndParams(@PathVariable String sourceId, @RequestParam("source") String source,
            @RequestParam(value = "id", required = false) Long id, HttpServletResponse response) throws IOException {
        log.info("getBidByPathAndParams: sourceId=" + sourceId + ", source=" + source + ", id=" + id);
        BidDto bidDto = null;
        List<BidDto> bidDtos = bidService.getBids(sourceId, source);
        if (id != null)
            bidDtos = bidDtos.stream()
                .filter((b) -> b.getId() == id)
                .collect(Collectors.toList());
        if (bidDtos == null)
            // use setStatus() instead of sendError() because it allows
            // ResponseBody to be written
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        else if (bidDtos.size() > 1)
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        else
            bidDto = bidDtos.get(0);
        return bidDto;
    }

}
