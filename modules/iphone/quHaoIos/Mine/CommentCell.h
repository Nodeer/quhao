//
//  CommentCell.h
//  quHaoIos
//
//  Created by sam on 14-3-27.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CommentModel.h"
#import "Helper.h"
@interface CommentCell : UITableViewCell
{
    UILabel *_titleLabel;
    UILabel *_label;
    UILabel *_timeLabel;
}
@property(nonatomic,strong) CommentModel *commentModel;

@end
