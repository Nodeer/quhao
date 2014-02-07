//
//  HomeViewController.h
//  quHaoIos
//
//  Created by sam on 13-10-5.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ASIHTTPRequest.h"
#import "Helper.h"
#import "ListViewController.h"
#import "SBJson.h"
#import "Category.h"
#import "UICustomLabel.h"
#import "SearchView.h"
#import "UICustomImageView.h"

@interface HomeViewController : UIViewController{
    NSMutableArray *categoryArray;
    NSMutableArray *topArray;
    int _columns;
    int _columnInc;
    CGFloat _marginSize;
    CGFloat _gutterSize;
    CGFloat _rowHeight;
    CGFloat _xOffset;
    CGFloat _yOffset;
    UIInterfaceOrientation _rotation;
}

@property (nonatomic, strong) UIScrollView *menuView;
@property (nonatomic, assign) BOOL wrap;
@property (nonatomic, strong) NSMutableArray *items;
//@property (nonatomic, strong) iCarousel *carousel;

-(UIScrollView *) setInitWithColumns:(int)col marginSize:(CGFloat)margin gutterSize:(CGFloat)gutter rowHeight:(CGFloat)height;
-(UIControl *) createMenuItem:(Category *)cate;
-(void)menuSetOrReset;
-(void)populateMenu;
-(void)loadNavigationItem;
-(void)clickSearch:(id)sender;
-(void)onClickUIImage:(UITapGestureRecognizer *)sender;
-(void)onClickUILable:(UITapGestureRecognizer *)sender;
-(void)pushMerchartDetail:(NSString *)cateType andNavController:(UINavigationController *)navController;

@end
