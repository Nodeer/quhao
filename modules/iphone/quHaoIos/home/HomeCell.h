//
//  HomeCell.h
//  quHaoApp
//
//  Created by sam on 13-7-29.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "EGOImageView.h"
#import "Helper.h"
@class MerchartModel;
@interface HomeCell : UITableViewCell
{
    @private
    UILabel *_titleLabel;
    UILabel *_yearLabel;
    UIView *_ratingView;
    MerchartModel *_merchartModel;    
}

@property(nonatomic,strong) MerchartModel *merchartModel;
@property (strong,nonatomic) EGOImageView * egoImgView;

@end
