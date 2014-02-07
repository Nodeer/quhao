//
//  HistoryCell.h
//  quHaoIos
//
//  Created by sam on 13-11-10.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MerchartModel.h"
#import "MerchartDetail.h"
#import "EGOImageView.h"
@interface HistoryCell : UITableViewCell
{
    UILabel *_titleLabel;
    UILabel *pjLabel;
    Reservation *_reservationModel;
    
}
@property(nonatomic,strong) Reservation *reservationModel;
@property (strong,nonatomic) EGOImageView * egoImgView;

@end
