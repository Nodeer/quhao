//
//  HomeCell.m
//  quHaoApp
//
//  Created by sam on 13-7-29.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "HomeCell.h"
#import "MerchartModel.h"
@implementation HomeCell

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
    
    self.egoImgView = [[EGOImageView alloc] initWithFrame:CGRectMake(5, 10, 105, 80)];
    [self.contentView addSubview:self.egoImgView];
    
    
    _titleLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    _titleLabel.backgroundColor=[UIColor clearColor];
    _titleLabel.textColor=[UIColor blackColor];
    _titleLabel.font=[UIFont boldSystemFontOfSize:18];
    [self.contentView addSubview:_titleLabel];
    
    
    _yearLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    _yearLabel.backgroundColor=[UIColor whiteColor];
    [self.contentView addSubview:_yearLabel];
    
    _ratingView=[[UILabel alloc]initWithFrame:CGRectZero];
    _ratingView.backgroundColor=[UIColor redColor];
    [self.contentView addSubview:_ratingView];
    
}

-(void) setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
}

-(void)layoutSubviews
{
    [super layoutSubviews];
    if (![[Helper returnUserString:@"showImage"] boolValue]||[self.merchartModel.imgUrl isEqualToString:@""])
    {
        self.egoImgView.image = [UIImage imageNamed:@"no_logo.png"];
    }
    else
    {
        self.egoImgView.imageURL = [NSURL URLWithString:self.merchartModel.imgUrl];
    }
    
    _titleLabel.frame=CGRectMake(self.egoImgView.frame.origin.x+self.egoImgView.frame.size.width+5, 10, 200, 30);
    _titleLabel.text=self.merchartModel.name;
    
    _yearLabel.frame=CGRectMake(_titleLabel.frame.origin.x, _titleLabel.frame.origin.y+_titleLabel.frame.size.height+3, _titleLabel.frame.size.width, 35);
    _yearLabel.text=self.merchartModel.distance;
    _yearLabel.textAlignment=NSTextAlignmentRight;
    _yearLabel.font=[UIFont boldSystemFontOfSize:13];
}
-(void)dealloc
{
    _titleLabel=nil;
    _yearLabel=nil;
    _ratingView=nil;
}
@end
