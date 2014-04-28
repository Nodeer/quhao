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
#import "MerchartModel.h"
@interface HomeCell : UITableViewCell
{
    @private
    UILabel *_titleLabel;
    UILabel *_disLabel;
    UILabel *_statusLabel;
    UILabel *_rjLabel;
    MerchartModel *_merchartModel;
}

@property(nonatomic,strong) MerchartModel *merchartModel;
@property (strong,nonatomic) EGOImageView * egoImgView;

@end
