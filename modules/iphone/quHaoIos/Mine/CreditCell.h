//
//  CreditCell.h
//  quHaoIos
//
//  Created by sam on 13-11-24.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Credit.h"
@interface CreditCell : UITableViewCell
{
    UILabel *_titleLabel;
    UILabel *_pjLabel;
    UILabel *_timeLabel;    
}
@property(nonatomic,strong) Credit *creditModel;

@end
