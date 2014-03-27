//
//  CommentCell.m
//  quHaoIos
//
//  Created by sam on 14-3-27.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "CommentCell.h"

@implementation CommentCell
@synthesize commentModel;

-(id) initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self=[super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if(self){
        [self initSubviews];
        self.accessoryType=UITableViewCellAccessoryDetailDisclosureButton;
    }
    return self;
}

-(void)initSubviews
{
    _titleLabel = [Helper getCustomLabel:@"" font:15 rect:CGRectMake(5, 5, 300, 20)];
    [self.contentView addSubview:_titleLabel];
    
    _timeLabel = [Helper getCustomLabel:@"" font:12 rect:CGRectMake(180, _titleLabel.frame.origin.y+_titleLabel.frame.size.height, 150, 10)];
    _timeLabel.font=[UIFont systemFontOfSize:12];
    [self.contentView  addSubview:_timeLabel];

    _label = [[UILabel alloc] initWithFrame:CGRectZero];
    [_label setLineBreakMode:NSLineBreakByWordWrapping];
    [_label setMinimumScaleFactor:14];
    [_label setNumberOfLines:0];
    _label.backgroundColor=[UIColor clearColor];
    [_label setFont:[UIFont systemFontOfSize:14]];
    [_label setTag:1];
    [self.contentView addSubview:_label];

}

-(void) setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
}

-(void)layoutSubviews
{
    [super layoutSubviews];
    _titleLabel.text = commentModel.merchantName;
    
    _timeLabel.text = [Helper formatDate:commentModel.created];
    
    CGSize constraint = CGSizeMake(kDeviceWidth - (CELL_CONTENT_MARGIN * 2), 20000.0f);
    NSAttributedString *attributedText = [[NSAttributedString alloc]initWithString:commentModel.content attributes:@{
                                                                                                           NSFontAttributeName:[UIFont systemFontOfSize:14]
                                                                                                           }];
    CGRect rect = [attributedText boundingRectWithSize:constraint
                                               options:NSStringDrawingUsesLineFragmentOrigin
                                               context:nil];
    CGSize size = rect.size;
    if (!_label)
        _label = (UILabel*)[self viewWithTag:1];
    [_label setText:commentModel.content];
    [_label setFrame:CGRectMake(CELL_CONTENT_MARGIN, CELL_CONTENT_MARGIN+28, kDeviceWidth - (CELL_CONTENT_MARGIN * 2), MAX(size.height, 44.0f))];
}
@end