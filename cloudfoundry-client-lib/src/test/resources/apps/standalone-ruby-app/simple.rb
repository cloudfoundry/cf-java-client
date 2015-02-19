if RUBY_VERSION =~ /\A1.8/
  print "running version 1.8"
elsif RUBY_VERSION =~ /\A1.9/
  print "running version 1.9"
else
  print "unexpected ruby version #{RUBY_VERSION}"
end
loop {
  sleep 100
}
