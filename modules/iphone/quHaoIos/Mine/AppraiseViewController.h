//
//  AppraiseViewController.h
//  quHaoIos
//
//  Created by sam on 13-11-13.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RatingControl.h"
#import "Helper.h"
#import <QuartzCore/CALayer.h>
#import "SBJsonParser.h"
#import "QuHaoUtil.h"
#import "UIScrollView+ScrollViewCategory.h"
@interface AppraiseViewController : UIViewController<RatingDelegate,UITextViewDelegate,UITextFieldDelegate>
@property (strong,nonatomic)RatingControl *customNumberOfStars;
@property (strong,nonatomic)RatingControl *fwNumberOfStars;
@property (strong,nonatomic)RatingControl *hjNumberOfStars;
@property (strong,nonatomic)RatingControl *xjbNumberOfStars;
@property (strong,nonatomic)RatingControl *ztNumberOfStars;
@property (strong,nonatomic) NSString * merchartID;
@property (strong,nonatomic) NSString *accouId;
@property (strong,nonatomic) NSString *rid;
@property (nonatomic, strong) UITextView *textView;
@property (nonatomic, strong) UITextField *accountField;
@property BOOL isCommented;

//提交评价
- (void)clickXx:(id)sender;
//获取已评论的信息
-(void)getComment;
//设置评价项
-(void)setRating:(RatingControl*)control;

@end
