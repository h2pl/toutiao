package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 周杰伦 on 2018/5/5.
 */
@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsService newsService;

    @Autowired
    private AliService aliService;

    @Autowired
    private QiniuService qiniuService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = {"/news/{newsId}"}, method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId, Model model) {
        News news = newsService.getById(newsId);
        if (news != null) {
            int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
            if (localUserId != 0) {
                model.addAttribute("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
            } else {
                model.addAttribute("like", 0);
            }
            // 评论
            List<Comment> comments = commentService.getCommentsByEntity(news.getId(), EntityType.ENTITY_NEWS);
            List<ViewObject> commentVOs = new ArrayList<ViewObject>();
            for (Comment comment : comments) {
                ViewObject vo = new ViewObject();
                vo.set("comment", comment);
                vo.set("user", userService.getUser(comment.getUserId()));
                commentVOs.add(vo);
            }
            model.addAttribute("comments", commentVOs);
        }
        model.addAttribute("news", news);
        model.addAttribute("owner", userService.getUser(news.getUserId()));
        return "detail";
    }

    @RequestMapping(path = {"/user/addNews/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link) {
        try {
            News news = new News();
            news.setCreatedDate(new Date());
            news.setTitle(title);
            news.setImage(image);
            news.setLink(link);
            if (hostHolder.getUser() != null) {
                news.setUserId(hostHolder.getUser().getId());
            } else {
                // 设置一个匿名用户
                news.setUserId(3);
            }
            newsService.addNews(news);
            return ToutiaoUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("添加资讯失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "发布失败");
        }
    }

    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content) {
        try {
            Comment comment = new Comment();
            comment.setUserId(hostHolder.getUser().getId());
            comment.setContent(content);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setEntityId(newsId);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            commentService.addComment(comment);

            // 更新评论数量，以后用异步实现
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            newsService.updateCommentCount(comment.getEntityId(), count);

        } catch (Exception e) {
            logger.error("提交评论错误" + e.getMessage());
        }
        return "redirect:/news/" + String.valueOf(newsId);
    }

    @RequestMapping("/uploadImage/")
    @ResponseBody
    //这里使用阿里oss，有空把七牛云搞一下
    public String uploadOSS(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = aliService.saveImage(file);
            if (fileUrl == null) {
                //先错误后正确
                return ToutiaoUtil.getJSONString(1, "上传图片失败");
            }
            return ToutiaoUtil.getJSONString(0, fileUrl);
        } catch (IOException e) {
            logger.error("上传失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "上传失败");
        }
    }

    @RequestMapping("/uploadQiniu/")
    @ResponseBody
    //这里使用阿里oss，有空把七牛云搞一下
    public String uploadQiniu(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = qiniuService.saveImage(file);
            if (fileUrl == null) {
                //先错误后正确
                return ToutiaoUtil.getJSONString(1, "上传图片失败");
            }
            return ToutiaoUtil.getJSONString(0, fileUrl);
        } catch (IOException e) {
            logger.error("上传失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "上传失败");
        }
    }

//    @RequestMapping("/uploadImage/")
//    @ResponseBody
//    public String uploadImage(@RequestParam("file") MultipartFile file) {
//        try {
//            String fileUrl = newsService.saveImage(file);
//            if (fileUrl == null) {
//                //先错误后正确
//                return ToutiaoUtil.getJSONString(1, "上传图片失败");
//            }
//            return ToutiaoUtil.getJSONString(0, fileUrl);
//        } catch (IOException e) {
//            logger.error("上传失败" + e.getMessage());
//            return ToutiaoUtil.getJSONString(1, "上传失败");
//        }
//    }
//
//    @RequestMapping("/uploadImages/")
//    @ResponseBody
//    public String uploadImages(@RequestParam("files") MultipartFile[] files) {
//        try {
//            String fileUrl = newsService.saveImages(files);
//            if (fileUrl == null) {
//                //先错误后正确
//                return ToutiaoUtil.getJSONString(1, "上传图片失败");
//            }
//            return ToutiaoUtil.getJSONString(0, fileUrl);
//        } catch (IOException e) {
//            logger.error("上传失败" + e.getMessage());
//            return ToutiaoUtil.getJSONString(1, "上传失败");
//        }
//    }

    @RequestMapping(path = {"/image/"}, method = {RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName,
                         HttpServletResponse response) {
        try {
            response.setContentType("image/jpg");
            StreamUtils.copy(new FileInputStream(new
                    File(ToutiaoUtil.IMAGE_DIR + imageName)), response.getOutputStream());
        } catch (Exception e) {
            logger.error("读取图片错误" + imageName + e.getMessage());
        }
    }

}
